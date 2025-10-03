<template>
  <div class="result-view">
    <nav class="breadcrumb">
      <router-link to="/">
        <span class="material-icons">home</span>
        Exams
      </router-link>
      <span class="separator">|</span>
      <router-link to="/import">
        <span class="material-icons">upload_file</span>
        Import Exam
      </router-link>
    </nav>

    <div class="result-header">
      <h1>Exam Results</h1>
      <div class="score-summary">
        <div class="score-card">
          <div class="score-number">{{ scoreNum }}/{{ totalNum }}</div>
          <div class="score-label">Score</div>
        </div>
        <div class="score-card">
          <div class="score-number">{{ (scoreNum / totalNum * 100).toFixed(1) }}%</div>
          <div class="score-label">Percentage</div>
        </div>
        <div class="score-card">
          <div class="score-number">{{ scoreNum === totalNum ? 'Pass' : 'Review' }}</div>
          <div class="score-label">Status</div>
        </div>
      </div>
    </div>

    <div class="questions-review">
      <div v-for="(question, index) in examQuestions" :key="index" class="question-card">
        <div class="question-header">
          <span class="question-number">Question {{ index + 1 }}</span>
          <span class="question-status" :class="getQuestionStatus(question)">
            <span class="material-icons">{{ getQuestionIcon(question) }}</span>
            {{ getQuestionStatusText(question) }}
          </span>
        </div>

        <p class="question-text">{{ question.text }}</p>
        
        <div class="choices">
          <div v-for="choice in question.choices" :key="choice.id" 
               :class="['choice', {
                 'selected': isChoiceSelected(question.id, choice.id),
                 'selected-correct': isChoiceSelected(question.id, choice.id) && choice.correct,
                 'selected-incorrect': isChoiceSelected(question.id, choice.id) && !choice.correct,
                 'correct-answer': !isChoiceSelected(question.id, choice.id) && choice.correct
               }]">
            <div class="choice-content">
              <span class="choice-marker">
                <span v-if="isChoiceSelected(question.id, choice.id)" class="material-icons" 
                      :class="{ 'icon-correct': choice.correct, 'icon-incorrect': !choice.correct }">
                  {{ choice.correct ? 'check_circle' : 'cancel' }}
                </span>
                <span v-else-if="choice.correct" 
                      class="material-icons icon-correct">
                  check_circle
                </span>
                <span v-else class="material-icons unchecked">
                  radio_button_unchecked
                </span>
              </span>
              <span class="choice-text">{{ choice.text }}</span>
            </div>
            <div class="choice-status">
              <span v-if="isChoiceSelected(question.id, choice.id)" class="status-label" 
                    :class="{ 'correct': choice.correct, 'incorrect': !choice.correct }">
                {{ choice.correct ? 'Your answer (Correct)' : 'Your answer (Incorrect)' }}
              </span>
              <span v-else-if="choice.correct" class="status-label correct">
                Correct answer
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="action-buttons">
      <router-link to="/" class="back-btn">
        <span class="material-icons">arrow_back</span>
        Back to Exams
      </router-link>
      <button class="retry-btn" @click="retryExam">
        <span class="material-icons">refresh</span>
        Try Again
      </button>
    </div>
  </div>
</template>

<script>
import { useExamStore } from '../stores/exam'
import { computed } from 'vue'
import { useRouter } from 'vue-router'

export default {
  props: {
    score: {
      type: String,
      required: true
    },
    total: {
      type: String,
      required: true
    }
  },
  setup() {
    const examStore = useExamStore()
    const router = useRouter()
    
    const getQuestionStatus = (question) => {
      const userAnswer = examStore.userAnswers.find(a => a.questionId === question.id)
      if (!userAnswer) return 'unanswered'
      
      const selectedChoice = question.choices.find(c => c.id === userAnswer.choiceId)
      return selectedChoice?.correct ? 'correct' : 'incorrect'
    }

    const getQuestionIcon = (question) => {
      const status = getQuestionStatus(question)
      switch (status) {
        case 'correct': return 'check_circle'
        case 'incorrect': return 'cancel'
        default: return 'help'
      }
    }

    const getQuestionStatusText = (question) => {
      const status = getQuestionStatus(question)
      switch (status) {
        case 'correct': return 'Correct'
        case 'incorrect': return 'Incorrect'
        default: return 'Not Answered'
      }
    }

    const isChoiceSelected = (questionId, choiceId) => {
      return examStore.userAnswers.some(answer => 
        answer.questionId === questionId && answer.choiceId === choiceId
      )
    }

    const retryExam = () => {
      const currentExamId = examStore.currentExam?.id
      if (currentExamId) {
        examStore.clearExamState()
        router.push(`/exam/${currentExamId}`)
      }
    }

    return {
      examQuestions: computed(() => examStore.currentExam?.questions || []),
      isChoiceSelected,
      getQuestionStatus,
      getQuestionIcon,
      getQuestionStatusText,
      retryExam
    }
  },
  computed: {
    scoreNum() {
      return parseInt(this.score)
    },
    totalNum() {
      return parseInt(this.total)
    }
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/icon?family=Material+Icons');

.result-view {
  padding: 2rem;
  max-width: 900px;
  margin: 0 auto;
  background: #f8f9fa;
  min-height: 100vh;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 0;
  margin-bottom: 2rem;
  border-bottom: 1px solid #e9ecef;
}

.breadcrumb a {
  color: #6c757d;
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.95rem;
}

.breadcrumb .material-icons {
  font-size: 1.2rem;
}

.breadcrumb a:hover {
  color: #4CAF50;
}

.separator {
  color: #dee2e6;
}

.result-header {
  text-align: center;
  margin-bottom: 3rem;
}

.result-header h1 {
  color: #212529;
  margin-bottom: 2rem;
  font-size: 2.5rem;
}

.score-summary {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.score-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  text-align: center;
}

.score-number {
  font-size: 2rem;
  font-weight: bold;
  color: #212529;
  margin-bottom: 0.5rem;
}

.score-label {
  color: #6c757d;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.questions-review {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.question-card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.question-number {
  font-weight: 600;
  color: #495057;
  font-size: 1.1rem;
}

.question-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
}

.question-status.correct {
  background: #e8f5e9;
  color: #2e7d32;
}

.question-status.incorrect {
  background: #ffebee;
  color: #c62828;
}

.question-status.unanswered {
  background: #f5f5f5;
  color: #757575;
}

.question-text {
  font-size: 1.1rem;
  color: #212529;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.choices {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.choice {
  padding: 1rem;
  border-radius: 8px;
  border: 2px solid #e9ecef;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  transition: all 0.3s ease;
  position: relative;
  background: white;
  margin-bottom: 0.5rem;
}

.choice.selected {
  background-color: #f8f9fa;
}

.choice.selected-correct {
  border-color: #4CAF50;
  background-color: #e8f5e9;
}

.choice.selected-incorrect {
  border-color: #f44336;
  background-color: #ffebee;
}

.choice.correct-answer {
  border: 2px dashed #4CAF50;
  background-color: rgba(232, 245, 233, 0.5);
}

.choice-content {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.choice-marker {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
}

.choice-marker .material-icons {
  font-size: 1.4rem;
  transition: all 0.3s ease;
}

.icon-correct {
  color: #4CAF50;
}

.icon-incorrect {
  color: #f44336;
}

.unchecked {
  color: #9e9e9e;
}

.choice-text {
  flex: 1;
  font-size: 1rem;
  line-height: 1.5;
}

.choice-status {
  margin-top: 0.5rem;
  text-align: right;
}

.status-label {
  font-size: 0.85rem;
  padding: 4px 12px;
  border-radius: 12px;
  font-weight: 500;
}

.status-label.correct {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-label.incorrect {
  background-color: #ffebee;
  color: #c62828;
}

.choice.selected.correct {
  background: #e8f5e9;
  border-color: #4CAF50;
}

.choice.selected.incorrect {
  background: #ffebee;
  border-color: #f44336;
}

.choice.not-selected {
  background: #f8f9fa;
  border-color: #e9ecef;
}

.choice-text {
  flex: 1;
  color: #212529;
}

.choice-status {
  display: flex;
  justify-content: flex-end;
}

.correct-answer, .your-answer {
  font-size: 0.8rem;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
}

.correct-answer {
  background: #e8f5e9;
  color: #2e7d32;
}

.your-answer {
  background: #ffebee;
  color: #c62828;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-top: 3rem;
  padding-bottom: 2rem;
}

.back-btn, .retry-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn {
  background: #6c757d;
  color: white;
  text-decoration: none;
}

.retry-btn {
  background: #4CAF50;
  color: white;
  border: none;
}

.back-btn:hover {
  background: #5a6268;
}

.retry-btn:hover {
  background: #45a049;
}

.material-icons {
  font-size: 1.2rem;
}
</style>
