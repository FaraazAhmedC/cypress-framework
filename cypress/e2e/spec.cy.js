// // describe('template spec', () => {
// //   it('passes', () => {
// //     cy.visit('https://github.com/')
// //   })
// // })

// describe('template spec', () => {
//   it('Gets, types and asserts', () => {
//     cy.visit('https://example.cypress.io/')

//     cy.contains('type').click()

//     // Should be on a new URL which
//     // includes '/commands/actions'
//     cy.url().should('include', '/commands/actions')

//     // Get an input, type into it
//     cy.get('.action-email').type('fake@email.com')

//     //  Verify that the value has been updated
//     cy.get('.action-email').should('have.value', 'fake@email.com')

//     cy.get('.action-focus').focus()
//     cy.get('.action-focus').should('have.class', 'focus')
//       .prev().should('have.attr', 'style', 'color: orange;')

//       cy.get('.action-btn').click()

//       // You can click on 9 specific positions of an element:
//       //  -----------------------------------
//       // | topLeft        top       topRight |
//       // |                                   |
//       // |                                   |
//       // |                                   |
//       // | left          center        right |
//       // |                                   |
//       // |                                   |
//       // |                                   |
//       // | bottomLeft   bottom   bottomRight |
//       //  -----------------------------------
      
//       // clicking in the center of the element is the default
//       cy.get('#action-canvas').click()
      
//       cy.get('#action-canvas').click('topLeft')
//       cy.get('#action-canvas').click('top')
//       cy.get('#action-canvas').click('topRight')
//       cy.get('#action-canvas').click('left')
//       cy.get('#action-canvas').click('right')
//       cy.get('#action-canvas').click('bottomLeft')
//       cy.get('#action-canvas').click('bottom')
//       cy.get('#action-canvas').click('bottomRight')
      
//       // .click() accepts an x and y coordinate
//       // that controls where the click occurs :)
      
//       cy.get('#action-canvas')
//       cy.get('#action-canvas').click(80, 75) // click 80px on x coord and 75px on y coord
//       cy.get('#action-canvas').click(170, 75)
//       cy.get('#action-canvas').click(80, 165)
//       cy.get('#action-canvas').click(100, 185)
//       cy.get('#action-canvas').click(125, 190)
//       cy.get('#action-canvas').click(150, 185)
//       cy.get('#action-canvas').click(170, 165)
      
//       // click multiple elements by passing multiple: true
//       cy.get('.action-labels>.label').click({ multiple: true })
      
//       // Ignore error checking prior to clicking
//       cy.get('.action-opacity>.btn').click({ force: true })

//       // Our app has a listener on 'dblclick' event in our 'scripts.js'
// // that hides the div and shows an input on double click
// cy.get('.action-div').dblclick()
// cy.get('.action-div').should('not.be.visible')
// cy.get('.action-input-hidden').should('be.visible')

// // Our app has a listener on 'contextmenu' event in our 'scripts.js'
// // that hides the div and shows an input on right click
// cy.get('.rightclick-action-div').rightclick()
// cy.get('.rightclick-action-div').should('not.be.visible')
// cy.get('.rightclick-action-input-hidden').should('be.visible')
// // By default, .uncheck() will uncheck all matching
// // checkbox elements in succession, one after another
// cy.get('.action-check [type="checkbox"]')
//   .not('[disabled]')
//   .uncheck()
// cy.get('.action-check [type="checkbox"]')
//   .not('[disabled]')
//   .should('not.be.checked')

// // .uncheck() accepts a value argument
// cy.get('.action-check [type="checkbox"]')
//   .check('checkbox1')
// cy.get('.action-check [type="checkbox"]')
//   .uncheck('checkbox1')
// cy.get('.action-check [type="checkbox"][value="checkbox1"]')
//   .should('not.be.checked')

// // .uncheck() accepts an array of values
// cy.get('.action-check [type="checkbox"]')
//   .check(['checkbox1', 'checkbox3'])
// cy.get('.action-check [type="checkbox"]')
//   .uncheck(['checkbox1', 'checkbox3'])
// cy.get('.action-check [type="checkbox"][value="checkbox1"]')
//   .should('not.be.checked')
// cy.get('.action-check [type="checkbox"][value="checkbox3"]')
//   .should('not.be.checked')

// // Ignore error checking prior to unchecking
// cy.get('.action-check [disabled]').uncheck({ force: true })
// cy.get('.action-check [disabled]').should('not.be.checked')
// cy.get('.action-blur').type('About to blur')
// cy.get('.action-blur').blur()
// cy.get('.action-blur').should('have.class', 'error')
//   .prev().should('have.attr', 'style', 'color: red;')
    
//   })
// })

describe('My First Test', () => {
  it('finds the content "type" and handles "hype" safely', () => {
    cy.visit('https://example.cypress.io')

    // This will fail the test if "type" is not found (normal behavior)
    cy.contains('type')
    cy.contains('hype')

      }
    })
  })
})
