package com.intelizign.career;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.*;

import com.intelizign.career.controller.JobController;
import com.intelizign.career.dto.JobDTO;
import com.intelizign.career.dto.KeySkillDTO;
import com.intelizign.career.dto.LocationDTO;
import com.intelizign.career.exception.CustomExceptions.DuplicateResourceException;
import com.intelizign.career.exception.CustomExceptions.ResourceNotFoundException;
import com.intelizign.career.model.Job;
import com.intelizign.career.model.KeySkill;
import com.intelizign.career.model.Location;
import com.intelizign.career.model.Recruiter;
import com.intelizign.career.repository.JobRepository;
import com.intelizign.career.repository.RecruiterRepository;
import com.intelizign.career.request.PostJobRequest;
import com.intelizign.career.service.JobService;
import com.intelizign.career.response.ResponseHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

class JobControllerTest {

    @InjectMocks
    private JobController jobController;

    @Mock
    private JobService jobService;

    @Mock
    private RecruiterRepository recruiterRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(userDetails.getUsername()).thenReturn("bino.06@example.com");
    }

    // Helper method to create mock PostJobRequest with location and key skills
    private PostJobRequest createMockPostJobRequest() {
        PostJobRequest request = new PostJobRequest();
        request.setJobTitle("Software Engineer");
        request.setJobDescription("Develop software");
        request.setJobRole("Developer");
        request.setIndustryType("IT");
        request.setDepartment("Engineering");
        request.setEmploymentType("Full-time");
        request.setRoleCategory("IT");
        request.setJobExperience("3 years");
        request.setEducation("Bachelor");
        request.setShortDescription("Software development role");
        request.setLocation(Collections.singletonList(new Location(1L, "New York")));
        request.setKeySkills(Collections.singletonList(new KeySkill(1L, "Java")));
        return request;
    }

    //Helper method to create mock Recruiter
    private Recruiter createMockRecruiter() {
        Recruiter recruiter = new Recruiter();
        recruiter.setEmail("recruiter@example.com");
        recruiter.setFirstName("John");
        recruiter.setMobile("1234567890");
        return recruiter;
    }

    // Helper method to create a Job (simplified)
    private Job createMockJob() {
        Job job = new Job();
        job.setId(1L);
        job.setJobTitle("Software Engineer");
        job.setJobDescription("Develop software");
        job.setLocation(Collections.singletonList(new Location(1L, "New York")));
        job.setRecruiterEmail("bino@example.com");
        return job;
    }

    @Test
    void testCreateJobSuccess() throws Exception {
        PostJobRequest request = createMockPostJobRequest();
        Recruiter recruiter = createMockRecruiter();

        when(recruiterRepository.findByEmail(any())).thenReturn(Optional.of(recruiter));
        when(jobRepository.existsByJobTitle(any())).thenReturn(false);
        when(jobService.createJob(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Object> response = jobController.createJob(userDetails, request);

        verify(jobService).createJob(any(Job.class));
        assert response.getStatusCode() == HttpStatus.OK;
        assert response.getBody() != null;
        // Verify message success
        Map body = (Map) response.getBody();
        assert body.toString().contains("Job Created Successfully");
    }

    @Test
    void testCreateJobAlreadyExistsInLocation() throws Exception {
        PostJobRequest request = createMockPostJobRequest();
        Recruiter recruiter = createMockRecruiter();

        Job foundJob = new Job();
        foundJob.setJobTitle(request.getJobTitle());
        foundJob.setLocation(Collections.singletonList(new Location(1L, "New York")));

        when(recruiterRepository.findByEmail(any())).thenReturn(Optional.of(recruiter));
        when(jobRepository.existsByJobTitle(request.getJobTitle())).thenReturn(true);
        when(jobRepository.findByJobTitle(request.getJobTitle())).thenReturn(Optional.of(foundJob));

        ResponseEntity<Object> response = jobController.createJob(userDetails, request);

        assert response.getStatusCode() == HttpStatus.OK;
        // Message: "Job already present in the location"
        Map body = (Map) response.getBody();
        assert body.toString().contains("Job already present in the location");
    }

    @Test
    void testCreateJobRecruiterNotFound() {
        PostJobRequest request = createMockPostJobRequest();

        when(recruiterRepository.findByEmail(any())).thenReturn(Optional.empty());

        try {
            jobController.createJob(userDetails, request);
            assert false; // Should not reach here
        } catch (ResourceNotFoundException ex) {
            assert ex.getMessage().equals("Recruiter not found");
        }
    }

    @Test
    void testCreateJobGeneralException() {
        PostJobRequest request = createMockPostJobRequest();
        Recruiter recruiter = createMockRecruiter();

        when(recruiterRepository.findByEmail(any())).thenReturn(Optional.of(recruiter));
        when(jobRepository.existsByJobTitle(any())).thenThrow(new RuntimeException("DB failure"));

        ResponseEntity<Object> response = jobController.createJob(userDetails, request);

        assert response.getStatusCode() == HttpStatus.OK;
        Map body = (Map) response.getBody();
        assert body.toString().contains("Error getting while job creation");
    }

    @Test
    void testGetJobByIdSuccess() {
        Job job = createMockJob();

        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        ResponseEntity<Object> response = jobController.getJobById(1L);

        assert response.getStatusCode() == HttpStatus.OK;
        Map body = (Map) response.getBody();
        assert body.toString().contains("Successfully Retrieved jobs by ID");
    }

    @Test
    void testGetJobByIdNotFound() {
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Object> response = jobController.getJobById(1L);

        assert response.getStatusCode() == HttpStatus.OK;
        Map body = (Map) response.getBody();
        assert body.toString().contains("Error getting retrieved jobs by id.");
    }

    @Test
    void testGetJobsByRecruiterSuccess() throws Exception {
        Recruiter recruiter = createMockRecruiter();
        when(recruiterRepository.findByEmail(any())).thenReturn(Optional.of(recruiter));

        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(1L);
        Page<JobDTO> page = new PageImpl<>(Collections.singletonList(jobDTO));

        when(jobRepository.getJobsByRecruiter(any(), any(), any())).thenReturn(page);
        when(jobRepository.findLocationsByJobId(1L)).thenReturn(Collections.singletonList(new LocationDTO()));
        when(jobRepository.findKeySkillsByJobId(1L)).thenReturn(Collections.singletonList(new KeySkillDTO()));

        ResponseEntity<Object> response = jobController.getJobsByRecruiter(userDetails, null, Pageable.unpaged());
        assert response.getStatusCode() == HttpStatus.OK;
        Map body = (Map) response.getBody();
        assert body.toString().contains("Retrieved Jobs By RecruiterId Successfully");
    }

//    @Test
//    void testGetJobsByRecruiterResourceNotFound() throws Exception {
//        when(recruiterRepository.findByEmail(any())).thenReturn(Optional.empty());
//
//        try {
//            jobController.getJobsByRecruiter(userDetails, null, Pageable.unpaged());
//            assert false;
//        } catch (ResourceNotFoundException ex) {
//            assert ex.getMessage().equals("Recruiter not found");
//        }
//    }

    @Test
    void testGetJobsByRecruiterException() {
        when(recruiterRepository.findByEmail(any())).thenReturn(Optional.of(createMockRecruiter()));
        when(jobRepository.getJobsByRecruiter(any(), any(), any())).thenThrow(new RuntimeException("DB failure"));

        ResponseEntity<Object> response = jobController.getJobsByRecruiter(userDetails, null, Pageable.unpaged());

        assert response.getStatusCode() == HttpStatus.OK;
        Map body = (Map) response.getBody();
        assert body.toString().contains("Error getting retrieved jobs by recruiterid.");
    }

    @Test
    void testGetAllJobsSuccess() {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setId(1L);
        Page<JobDTO> page = new PageImpl<>(Collections.singletonList(jobDTO));

        when(jobRepository.getAllActiveJobs(any(), any())).thenReturn(page);
        when(jobRepository.findLocationsByJobId(1L)).thenReturn(Collections.singletonList(new LocationDTO()));
        when(jobRepository.findKeySkillsByJobId(1L)).thenReturn(Collections.singletonList(new KeySkillDTO()));

        ResponseEntity<Object> response = jobController.getJobsByRecruiter(null, Pageable.unpaged());

        assert response.getStatusCode() == HttpStatus.OK;
        Map body = (Map) response.getBody();
        assert body.toString().contains("Retrieved Jobs Successfully");
    }

    @Test
    void testGetAllJobsException() {
        when(jobRepository.getAllActiveJobs(any(), any())).thenThrow(new RuntimeException("DB failure"));

        ResponseEntity<Object> response = jobController.getJobsByRecruiter(null, Pageable.unpaged());

        assert response.getStatusCode() == HttpStatus.OK;
        Map body = (Map) response.getBody();
        assert body.toString().contains("Error getting retrieved jobs by recruiterid.");
    }

    @Test
    void testUpdateJobSuccess() throws Exception {
        PostJobRequest request = createMockPostJobRequest();
        Job updatedJob = createMockJob();

        when(jobService.updateJob(eq(1L), any(PostJobRequest.class))).thenReturn(updatedJob);

        ResponseEntity<Object> response = jobController.updateJob(1L, request);

        assert response.getStatusCode() == HttpStatus.OK;
        Map body = (Map) response.getBody();
        assert body.toString().contains("Updated job successfully");
    }

    @Test
    void testUpdateJobException() throws Exception {
        PostJobRequest request = createMockPostJobRequest();

        when(jobService.updateJob(eq(1L), any(PostJobRequest.class))).thenThrow(new RuntimeException("DB error"));

        ResponseEntity<Object> response = jobController.updateJob(1L, request);

        assert response.getStatusCode() == HttpStatus.OK;
        Map body = (Map) response.getBody();
        assert body.toString().contains("Job updation failed");
    }

    @Test
    void testDeleteJobSuccess() {
        Job job = createMockJob();
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));

        doNothing().when(jobService).deleteJob(job);

        ResponseEntity<Object> response = jobController.deleteJob(1L);

        assert response.getStatusCode() == HttpStatus.OK;
        Map body = (Map) response.getBody();
        assert body.toString().contains("Job deleted successfully");
    }

    @Test
    void testDeleteJobNullId() {
        ResponseEntity<Object> response = jobController.deleteJob(null);
        assert response.getStatusCode() == HttpStatus.OK;
        Map body = (Map) response.getBody();
        assert body.toString().contains("Job ID must be provided");
    }

//    @Test
//    void testDeleteJobNotFound() {
//        when(jobRepository.findById(1L)).thenReturn(Optional.empty());
//
//        try {
//            jobController.deleteJob(1L);
//            assert false;
//        } catch (ResourceNotFoundException ex) {
//            assert ex.getMessage().contains("Job with ID 1 not found");
//        }
//    }

    @Test
    void testDeleteJobDataIntegrityViolation() {
        Job job = createMockJob();
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        doThrow(new DataIntegrityViolationException("Constraint violation")).when(jobService).deleteJob(job);

        try {
            jobController.deleteJob(1L);
            assert false;
        } catch (DuplicateResourceException ex) {
            assert ex.getMessage().contains("Cannot delete job with ID 1 due to database constraints");
        }
    }

    @Test
    void testDeleteJobGeneralException() {
        Job job = createMockJob();
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        doThrow(new RuntimeException("DB Error")).when(jobService).deleteJob(job);

        ResponseEntity<Object> response = jobController.deleteJob(1L);

        assert response.getStatusCode() == HttpStatus.OK;
        Map body = (Map) response.getBody();
        assert body.toString().contains("An unexpected error occurred while deleting job with ID");
    }
}