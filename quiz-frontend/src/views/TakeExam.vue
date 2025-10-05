<template>
  <div class="take-exam" v-if="exam">
    <!-- Sticky Header -->
    <div class="exam-header">
      <h1 class="exam-title">{{ exam.title }}</h1>
      <div class="progress-bar">
        <div class="progress-indicator" :style="{ width: progressPercentage + '%' }"></div>
        <span class="progress-text">Question {{ currentQuestionIndex + 1 }} of {{ exam.questions.length }}</span>
        <div v-if="exam.timeLimitSec" class="timer">
          <span class="timer-icon">⏱️</span>
          <span>{{ formattedTimeRemaining }}</span>
        </div>
      </div>
    </div>

    <!-- Description Card -->
    <div class="description-card">
      <p>{{ exam.description }}</p>
    </div>

    <!-- Main Content with Side Navigation -->
    <div class="exam-content">
      <!-- Question Navigation -->
      <div class="question-nav">
        <div v-for="(question, idx) in exam.questions" 
             :key="question.id"
             class="nav-item"
             :class="{
               'active': idx === currentQuestionIndex,
               'answered': isQuestionAnswered(question.id),
               'marked': markedQuestions[question.id]
             }"
             @click="navigateToQuestion(idx)">
          {{ idx + 1 }}
        </div>
      </div>

      <!-- Questions Area -->
      <div class="questions-container">
        <div v-for="(question, index) in exam.questions" 
             :key="question.id" 
             class="question"
             :class="{ 'active': index === currentQuestionIndex }"
             v-show="index === currentQuestionIndex">
          <div class="question-header">
            <h2>Question {{ index + 1 }}</h2>
            <label class="mark-review">
              <input type="checkbox" v-model="markedQuestions[question.id]">
              Mark for Review
            </label>
          </div>
          
          <p class="question-text">{{ question.text }}</p>
          
          <div class="choices">
            <template v-if="question.multiple">
              <div v-for="(choice, idx) in question.choices" 
                   :key="choice.id" 
                   class="choice-item"
                   :class="{ 'selected': isChoiceSelected(question.id, choice.id) }"
                   @click="toggleChoice(question.id, choice.id, question.multiple)">
                <div class="choice-content">
                  <input type="checkbox" 
                         :id="'choice-' + choice.id"
                         :checked="isChoiceSelected(question.id, choice.id)"
                         @click.stop>
                  <label :for="'choice-' + choice.id">{{ String.fromCharCode(65 + idx) }}. {{ choice.text }}</label>
                </div>
              </div>
            </template>
            <template v-else>
              <div v-for="(choice, idx) in question.choices" 
                   :key="choice.id" 
                   class="choice-item"
                   :class="{ 'selected': isChoiceSelected(question.id, choice.id) }"
                   @click="toggleChoice(question.id, choice.id, false)">
                <div class="choice-content">
                  <input type="radio" 
                         :id="'choice-' + choice.id"
                         :checked="isChoiceSelected(question.id, choice.id)"
                         :name="'question-' + question.id"
                         @click.stop>
                  <label :for="'choice-' + choice.id">{{ String.fromCharCode(65 + idx) }}. {{ choice.text }}</label>
                </div>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>

    <!-- Sticky Footer -->
    <div class="exam-footer">
      <div class="navigation-buttons">
        <button @click="previousQuestion" 
                :disabled="currentQuestionIndex === 0"
                class="nav-btn">
          Previous
        </button>
        <button @click="nextQuestion" 
                :disabled="currentQuestionIndex === exam.questions.length - 1"
                class="nav-btn">
          Next
        </button>
      </div>
      <div class="action-buttons">
        <button @click="saveProgress" class="save-btn">Save Progress</button>
        <button @click="confirmSubmit" 
                class="submit-btn"
                :disabled="!canSubmit">
          Submit Exam
        </button>
      </div>
    </div>

    <!-- Submit Confirmation Dialog -->
    <div v-if="showSubmitDialog" class="modal-overlay">
      <div class="modal-content">
        <h3>Submit Exam?</h3>
        <p>You have answered {{ answeredCount }} out of {{ exam.questions.length }} questions.</p>
        <p v-if="unansweredQuestions.length">
          Warning: {{ unansweredQuestions.length }} questions are still unanswered.
        </p>
        <div class="modal-actions">
          <button @click="showSubmitDialog = false" class="cancel-btn">Cancel</button>
          <button @click="submitExam" class="confirm-btn">Confirm Submit</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useExamStore } from '../stores/exam'
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'

const examStore = useExamStore()
const router = useRouter()

const exam = ref(null)
const answers = ref({})
const currentQuestionIndex = ref(0)
const markedQuestions = ref({})
const showSubmitDialog = ref(false)
const timeRemaining = ref(0)
let timer = null

// Computed Properties
const progressPercentage = computed(() => {
  if (!exam.value) return 0
  const answeredCount = Object.values(answers.value).filter(answer => 
    Array.isArray(answer) ? answer.length > 0 : answer !== null
  ).length
  return (answeredCount / exam.value.questions.length) * 100
})

const answeredCount = computed(() => {
  return Object.values(answers.value).filter(answer => 
    Array.isArray(answer) ? answer.length > 0 : answer !== null
  ).length
})

const unansweredQuestions = computed(() => {
  if (!exam.value) return []
  return exam.value.questions.filter(q => {
    const answer = answers.value[q.id]
    return Array.isArray(answer) ? answer.length === 0 : answer === null
  })
})

const canSubmit = computed(() => answeredCount.value > 0)

const formattedTimeRemaining = computed(() => {
  const minutes = Math.floor(timeRemaining.value / 60)
  const seconds = timeRemaining.value % 60
  return `${minutes}:${seconds.toString().padStart(2, '0')}`
})

// Methods
const navigateToQuestion = (index) => {
  currentQuestionIndex.value = index
}

const isQuestionAnswered = (questionId) => {
  const answer = answers.value[questionId]
  return Array.isArray(answer) ? answer.length > 0 : answer !== null
}

const isChoiceSelected = (questionId, choiceId) => {
  const answer = answers.value[questionId]
  if (Array.isArray(answer)) {
    return answer.includes(choiceId)
  }
  return answer === choiceId
}

const toggleChoice = (questionId, choiceId, isMultiple) => {
  if (isMultiple) {
    if (!Array.isArray(answers.value[questionId])) {
      answers.value[questionId] = []
    }
    const index = answers.value[questionId].indexOf(choiceId)
    if (index === -1) {
      answers.value[questionId].push(choiceId)
    } else {
      answers.value[questionId].splice(index, 1)
    }
  } else {
    answers.value[questionId] = choiceId
  }
}

const previousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

const nextQuestion = () => {
  if (currentQuestionIndex.value < exam.value.questions.length - 1) {
    currentQuestionIndex.value++
  }
}

const saveProgress = async () => {
  // TODO: Implement save progress functionality
  alert('Progress saved!')
}

const confirmSubmit = () => {
  showSubmitDialog.value = true
}

const submitExam = async () => {
  try {
    const detailedAnswers = exam.value.questions.map(question => {
      const selectedIds = answers.value[question.id]
      if (!selectedIds) return null

      const choiceIds = Array.isArray(selectedIds) ? selectedIds : [selectedIds]
      return {
        question: { id: question.id },
        selectedChoiceIds: choiceIds
      }
    }).filter(answer => answer !== null)

    const attempt = {
      examId: exam.value.id,
      answers: detailedAnswers
    }

    const result = await examStore.submitAttempt(attempt)

    if (result && typeof result.score === 'number' && typeof result.totalQuestions === 'number') {
      router.push(`/result/${result.score}/${result.totalQuestions}`)
    } else {
      throw new Error('Invalid result format')
    }
  } catch (error) {
    console.error('Error submitting exam:', error)
    alert('Error submitting exam. Please try again.')
  }
}

const startTimer = () => {
  if (!exam.value?.timeLimitSec) return
  
  timeRemaining.value = exam.value.timeLimitSec
  timer = setInterval(() => {
    if (timeRemaining.value > 0) {
      timeRemaining.value--
    } else {
      clearInterval(timer)
      submitExam()
    }
  }, 1000)
}

// Lifecycle Hooks
onMounted(async () => {
  try {
    const id = router.currentRoute.value.params.id
    exam.value = await examStore.fetchExamById(id)
    exam.value.questions.forEach(q => {
      answers.value[q.id] = q.multiple ? [] : null
      markedQuestions.value[q.id] = false
    })
    startTimer()
  } catch (error) {
    console.error('Error loading exam:', error)
    alert('Error loading exam. Please try again.')
    router.push('/')
  }
})

onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.take-exam {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f8f9fa;
}

/* Header Styles */
.exam-header {
  position: sticky;
  top: 0;
  background-color: white;
  padding: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.exam-title {
  font-size: 1.5rem;
  color: #1a1a1a;
  margin-bottom: 1rem;
}

.progress-bar {
  position: relative;
  height: 4px;
  background-color: #e9ecef;
  border-radius: 2px;
  margin: 1rem 0;
}

.progress-indicator {
  position: absolute;
  height: 100%;
  background-color: #4CAF50;
  border-radius: 2px;
  transition: width 0.3s ease;
}

.progress-text {
  position: absolute;
  right: 0;
  top: -20px;
  font-size: 0.875rem;
  color: #6c757d;
}

.timer {
  position: absolute;
  right: 0;
  top: 1rem;
  background-color: #f8f9fa;
  padding: 0.5rem 1rem;
  border-radius: 1rem;
  font-size: 0.875rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

/* Description Card */
.description-card {
  background-color: white;
  margin: 1rem auto;
  padding: 1.5rem;
  border-radius: 0.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  max-width: 800px;
  width: 100%;
}

/* Content Layout */
.exam-content {
  display: flex;
  gap: 2rem;
  padding: 1rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding-bottom: 5rem; /* Add bottom padding to avoid content hidden behind fixed footer */
}

/* Question Navigation */
.question-nav {
  position: sticky;
  top: 6rem;
  height: fit-content;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 0.5rem;
  padding: 1rem;
  background-color: white;
  border-radius: 0.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.nav-item {
  width: 2.5rem;
  height: 2.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 0.25rem;
  cursor: pointer;
  font-size: 0.875rem;
  border: 1px solid #dee2e6;
  transition: all 0.2s;
}

.nav-item.active {
  background-color: #4CAF50;
  color: white;
  border-color: #4CAF50;
}

.nav-item.answered {
  background-color: #d3d3d3; /* light gray */
  border-color: #a9a9a9; /* dark gray */
}

.nav-item.marked {
  border-color: #ffc107;
  background-color: #fff8e1;
}

/* Questions Container */
.questions-container {
  flex: 1;
  min-width: 0;
}

.question {
  background-color: white;
  padding: 2rem;
  border-radius: 0.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 1rem;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.question-text {
  font-size: 1.125rem;
  line-height: 1.6;
  color: #1a1a1a;
  margin-bottom: 1.5rem;
}

/* Choices */
.choices {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.choice-item {
  border: 1px solid #dee2e6;
  border-radius: 0.5rem;
  transition: all 0.2s;
  cursor: pointer;
}

.choice-content {
  display: flex;
  align-items: center;
  padding: 1rem;
  gap: 1rem;
}

.choice-item:hover {
  background-color: #f8f9fa;
  border-color: #4CAF50;
}

.choice-item.selected {
  background-color: #e8f5e9;
  border-color: #4CAF50;
}

/* Footer */
.exam-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: white;
  padding: 1rem 2rem;
  box-shadow: 0 -2px 6px rgba(0, 0, 0, 0.15);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  z-index: 1000;
}

.navigation-buttons,
.action-buttons {
  display: flex;
  gap: 1rem;
}

.nav-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #dee2e6;
  border-radius: 0.25rem;
  background-color: white;
  cursor: pointer;
  transition: all 0.2s;
}

.nav-btn:hover:not(:disabled) {
  background-color: #f8f9fa;
  border-color: #6c757d;
}

.save-btn,
.submit-btn {
  padding: 0.75rem 1.5rem;
  border-radius: 0.25rem;
  border: none;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.save-btn {
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
}

.save-btn:hover {
  background-color: #e9ecef;
}

.submit-btn {
  background-color: #4CAF50;
  color: white;
}

.submit-btn:hover:not(:disabled) {
  background-color: #45a049;
}

.submit-btn:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 20;
}

.modal-content {
  background-color: white;
  padding: 2rem;
  border-radius: 0.5rem;
  max-width: 500px;
  width: 90%;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.cancel-btn,
.confirm-btn {
  padding: 0.5rem 1rem;
  border-radius: 0.25rem;
  cursor: pointer;
  font-weight: 500;
}

.cancel-btn {
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
}

.confirm-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
}

/* Responsive Design */
@media (max-width: 768px) {
  .exam-content {
    flex-direction: column;
  }

  .question-nav {
    position: static;
    width: 100%;
  }

  .exam-footer {
    flex-direction: column;
    gap: 1rem;
  }

  .navigation-buttons,
  .action-buttons {
    width: 100%;
    justify-content: center;
  }
}

.mark-review {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #6c757d;
}
</style>
