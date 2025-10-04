export interface ImportExam {
  title: string;
  description?: string;
  timeLimit?: number | null;
  questions: Array<{
    id?: string | number;
    text: string;
    choices: Array<{
      text: string;
      correct: boolean;
      explanation?: string;
    }>;
  }>;
}

export interface NormalizedExam {
  title: string;
  description?: string;
  timeLimitSec?: number | null;
  questions: Array<{
    text: string;
    multiple: boolean;
    choices: Array<{
      text: string;
      correct: boolean;
      explanation?: string;
    }>;
  }>;
}

function stripBOM(text: string): string {
  return text.replace(/^\uFEFF/, '');
}

export function validateExam(text: string): {
  ok: boolean;
  errs: string[];
  warns: string[];
  normalized: NormalizedExam | null;
} {
  const errs: string[] = [];
  const warns: string[] = [];
  let normalized: NormalizedExam | null = null;

  try {
    const data: ImportExam = JSON.parse(stripBOM(text));

    // Check title
    if (!data.title || typeof data.title !== 'string' || data.title.trim() === '') {
      errs.push('Title is required and must be a non-empty string');
    }

    // Check questions
    if (!data.questions || !Array.isArray(data.questions) || data.questions.length === 0) {
      errs.push('Questions must be a non-empty array');
    } else {
      data.questions.forEach((q, idx) => {
        if (!q.text || typeof q.text !== 'string' || q.text.trim() === '') {
          errs.push(`Question ${idx + 1}: text is required and must be a non-empty string`);
        }
        if (!q.choices || !Array.isArray(q.choices) || q.choices.length < 2) {
          errs.push(`Question ${idx + 1}: choices must be an array with at least 2 items`);
        } else {
          q.choices.forEach((choice, choiceIdx) => {
            if (!choice.text || typeof choice.text !== 'string') {
              errs.push(`Question ${idx + 1}, choice ${choiceIdx + 1}: text is required and must be a string`);
            }
            if (typeof choice.correct !== 'boolean') {
              errs.push(`Question ${idx + 1}, choice ${choiceIdx + 1}: correct must be a boolean`);
            }
          });
        }
        // Validate at least one correct answer
        const correctCount = q.choices?.filter(c => c.correct).length || 0;
        if (correctCount === 0) {
          errs.push(`Question ${idx + 1}: at least one choice must be marked correct`);
        }
      });
    }

    // Warnings
    if (!data.description) {
      warns.push('Description is missing');
    }
    if (data.timeLimit === null || data.timeLimit === undefined) {
      warns.push('Time limit is missing');
    }

    // If no errors, normalize
    if (errs.length === 0) {
      normalized = {
        title: data.title.trim(),
        description: data.description?.trim(),
        timeLimitSec: data.timeLimit ? data.timeLimit * 60 : null,
        questions: data.questions.map((q, idx) => {
          const correctCount = q.choices.filter(c => c.correct).length;
          return {
            text: q.text.trim(),
            multiple: correctCount > 1,
            choices: q.choices.map(choice => ({
              text: choice.text.trim(),
              correct: choice.correct,
              explanation: choice.explanation?.trim()
            }))
          };
        })
      };
    }
  } catch (err: any) {
    errs.push('Invalid JSON: ' + (err?.message ?? 'Parse error'));
  }

  return {
    ok: errs.length === 0,
    errs,
    warns,
    normalized
  };
}
