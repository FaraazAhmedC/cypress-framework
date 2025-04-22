describe('GitHub Login Test', () => {
  it('should log in to GitHub', () => {

    const currTime = new Date().toLocaleString(); // Get the current date and time

    cy.visit('https://github.com/login');
    

    // Fill in username
    cy.get('#login_field').type('FaraazahmedC');

    // Fill in password
    cy.get('#password').type('Fara@2001');

    // Click the Sign in button
    cy.get('input[name="commit"]').click();

    // Assert login success (optional — depends on your credentials)
    cy.url().should('not.include', '/login'); // If login succeeds

    cy.visit('https://github.com/FaraazAhmedC/cypress-framework/blob/main/README.md');

    cy.get('[aria-label="Edit this file"]').click();

    // Step 4: Append the current date and time to the file
    cy.get('.cm-content[contenteditable="true"]')
      .find('.cm-line') // Find the existing lines
      .last() // Get the last line
      .type(`Edited time: ${currTime}`); // Append the current time

    // Step 5: Commit the change
    cy.get('button').contains('Commit changes...').click(); // Click the Commit changes button
    cy.get('#commit-message-input').clear().type(`Update README with current time: ${currTime}`); // Type commit message
    cy.focused().type('{enter}')
    // Step 6: Logout
    cy.get('img.avatar.circle').click();
    cy.get('a[href="/logout"]').click();
    cy.get('a[href="/logout"]').click();
    cy.get('input[type="submit"][value="Sign out"]').click();
    // cy.contains('button', 'Commit changes').then($btn => {
    //   console.log($btn[0].getBoundingClientRect()) // Logs position and size
    // })
    

  });
});