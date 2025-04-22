package com.intelizign.career.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler 
{
	public static ResponseEntity<Object> generateResponse(String message, Boolean status, HttpStatus statuscode,Object responseObj) 
	{
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("message", message);
		map.put("status", status);
		map.put("statuscode", statuscode.value());
		map.put("data", responseObj);
		return new ResponseEntity<>(map, statuscode);
	}
}
