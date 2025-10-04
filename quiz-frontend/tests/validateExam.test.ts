import { describe, it, expect } from 'vitest';
import { validateExam } from '../src/utils/validateExam';

describe('validateExam', () => {
  it('should validate and normalize a valid exam JSON', () => {
    const validJson = JSON.stringify({
      title: 'Sample Exam',
      description: 'A test exam',
      timeLimit: 30,
      questions: [
        {
          text: 'What is 2+2?',
          options: ['3', '4', '5'],
          answer: 1
        },
        {
          text: 'Select primes',
          options: ['2', '3', '4', '5'],
          answer: [0, 1, 3]
        }
      ]
    });

    const result = validateExam(validJson);

    expect(result.ok).toBe(true);
    expect(result.errs).toHaveLength(0);
    expect(result.warns).toHaveLength(0);
    expect(result.normalized).toEqual({
      title: 'Sample Exam',
      description: 'A test exam',
      timeLimitSec: 1800,
      questions: [
        {
          id: 1,
          text: 'What is 2+2?',
          multiple: false,
          choices: [
            { text: '3', correct: false, explanation: '' },
            { text: '4', correct: true, explanation: '' },
            { text: '5', correct: false, explanation: '' }
          ]
        },
        {
          id: 2,
          text: 'Select primes',
          multiple: true,
          choices: [
            { text: '2', correct: true, explanation: '' },
            { text: '3', correct: true, explanation: '' },
            { text: '4', correct: false, explanation: '' },
            { text: '5', correct: true, explanation: '' }
          ]
        }
      ]
    });
  });

  it('should fail on invalid JSON', () => {
    const invalidJson = '{ invalid json }';

    const result = validateExam(invalidJson);

    expect(result.ok).toBe(false);
    expect(result.errs).toContain('Invalid JSON:');
    expect(result.normalized).toBeNull();
  });

  it('should fail on invalid schema', () => {
    const invalidSchemaJson = JSON.stringify({
      title: '',
      questions: [
        {
          text: 'Question?',
          options: ['A'],
          answer: 2 // out of range
        }
      ]
    });

    const result = validateExam(invalidSchemaJson);

    expect(result.ok).toBe(false);
    expect(result.errs).toContain('Title is required and must be a non-empty string');
    expect(result.errs).toContain('Question 1: options must be an array with at least 2 items');
    expect(result.errs).toContain('Question 1: answer index 2 is out of range (options: 1)');
    expect(result.normalized).toBeNull();
  });
});
