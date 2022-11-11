import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom'
import Demo from '../MapDemo';
import React from 'react';

/**
 * integration tests on either the front-end (using React Testing Library) or back-end (using JUnit);  
infrastructure to support testing via mocks on either the front-end or back-end, and at least one test in your suite that shows this infrastructure working; and
unit testing for the bounding-box filtering requirement on map data.

 */


describe('testing Integration', () => {
    // before each test re-render REPL
    beforeEach(() => {
          render(<Demo />)
      })

    test('drag map', () => {
      userEvent.

    }
    )

    test('zoom map', () => {

    }
    )

      test('two valid get/stats', async () => {

        const command0 = screen.getByRole("textbox", {name: TEXT_command_accessible_name})
        const submitButton = screen.getByRole("button", {name: TEXT_try_button_accessible_name}) 
    
        userEvent.type(command0, 'get data/stars/two-stars.csv')
        userEvent.click(submitButton)
        await new Promise((r)=> setTimeout(r,900));
        let commandOutput = await screen.getAllByRole(/.*/, {name: TEXT_command_received_notification})
        expect(commandOutput[0]).toBeInTheDocument()
        
    
        userEvent.type(command0, 'stats')
        userEvent.click(submitButton)
        await new Promise((r)=> setTimeout(r,900));
        commandOutput = await screen.getAllByRole(/.*/, {name: TEXT_command_received_notification})
        expect(commandOutput[1]).toBeInTheDocument()
        var output = await screen.findByText('Output:"Rows: 2 Columns: 5"');
        expect(output).toBeInTheDocument()
    
    
        userEvent.type(command0, 'get data/stars/one-char.csv')
        userEvent.click(submitButton)
        await new Promise((r)=> setTimeout(r,600));
        commandOutput = await screen.getAllByRole(/.*/, {name: TEXT_command_received_notification})
     
        expect(commandOutput[2]).toBeInTheDocument()
        var output2 = await screen.findByText('Output:"[[a]]"');
        expect(output2).toBeInTheDocument()
    
        userEvent.type(command0, 'stats')
        userEvent.click(submitButton)
        //await new Promise((r)=> setTimeout(r,600));
        commandOutput = await screen.getAllByRole(/.*/, {name: TEXT_command_received_notification})
        expect(commandOutput[3]).toBeInTheDocument()
        var output3 = await screen.findByText('Output:"Rows: 1 Columns: 1"');
        expect(output3).toBeInTheDocument()
    })
    

})