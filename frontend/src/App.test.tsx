import React from 'react';
import { render, screen } from '@testing-library/react';
import App from './App';

test('renders instructions', () => {
  render(<App />);
  const instructionElement = screen.getByText(/react/i);
  expect(instructionElement).toBeInTheDocument();
});
