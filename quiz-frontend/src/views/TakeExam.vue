<template>
  <div class="take-exam" v-if="exam">
    <h2>{{ exam.title }}</h2>
    <p>{{ exam.description }}</p>
    <div v-for="(question, index) in exam.questions" :key="question.id" class="question">
      <h3>Question {{ index + 1 }}</h3>
      <p>{{ question.text }}</p>
      <div class="choices">
        <template v-if="question.multiple">
          <div v-for="choice in question.choices" :key="choice.id" class="choice-item">
            <input type="checkbox" 
                   :id="'choice-' + choice.id"
                   v-model="answers[question.id]"
                   :value="choice.id">
            <label :for="'choice-' + choice.id">{{ choice.text }}</label>
          </div>
        </template>
        <template v-else>
          <div v-for="choice in question.choices" :key="choice.id" class="choice-item">
            <input type="radio" 
                   :id="'choice-' + choice.id"
                   v-model="answers[question.id]"
                   :value="choice.id"
                   :name="'question-' + question.id">
            <label :for="'choice-' + choice.id">{{ choice.text }}</label>
          </div>
        </template>
      </div>
    </div>
    <button @click="submitExam" class="submit-btn">Submit Exam</button>
  </div>
</template>

<script>
import { useExamStore } from '../stores/exam'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

export default {
  setup() {
    const examStore = useExamStore()
    const router = useRouter()
    const exam = ref(null)
    const answers = ref({})

    onMounted(async () => {
      const id = router.currentRoute.value.params.id
      exam.value = await examStore.fetchExamById(id)
      exam.value.questions.forEach(q => {
        answers.value[q.id] = q.multiple ? [] : null
      })
    })

    const submitExam = async () => {
      try {
        // Store detailed answer information including the question and selected choice details
        const detailedAnswers = exam.value.questions.map(question => {
          const selectedId = answers.value[question.id]
          if (!selectedId) return null // Skip if no answer selected

          return {
            questionId: question.id,
            choiceId: selectedId
          }
        }).filter(answer => answer !== null)

        // Store the detailed answers in the store
        examStore.$patch(state => {
          state.userAnswers = detailedAnswers
        })

        // Format the answers for the API
        const formattedAnswers = detailedAnswers.map(answer => ({
          questionId: answer.questionId,
          choiceId: answer.choiceId,
        }));

        const attempt = {
          examId: exam.value.id,
          answers: formattedAnswers.map(answer => ({
            question: { id: answer.questionId },
            selectedChoiceIds: [answer.choiceId]
          }))
        }

        console.log('Submitting attempt:', attempt)
        // First store the user answers in the correct format
        examStore.$patch(state => {
          state.userAnswers = formattedAnswers
        })
        const result = await examStore.submitAttempt(attempt)
        console.log('Received result:', result)

        if (result && typeof result.score === 'number' && typeof result.totalQuestions === 'number') {
          router.push(`/result/${result.score}/${result.totalQuestions}`)
        } else {
          console.error('Invalid result format:', result)
          alert('Error submitting exam. Please try again.')
        }
      } catch (error) {
        console.error('Error submitting exam:', error)
        alert('Error submitting exam. Please try again.')
      }
    }

    return {
      exam,
      answers,
      submitExam
    }
  }
}
</script>

<style scoped>
.take-exam {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.question {
  margin: 30px 0;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

h2 {
  color: #2c3e50;
  margin-bottom: 10px;
}

h3 {
  color: #34495e;
  margin-bottom: 15px;
}

p {
  color: #2c3e50;
  line-height: 1.6;
}

.choices {
  margin: 15px 0;
}

.choice-item {
  margin: 10px 0;
  padding: 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.choice-item:hover {
  background-color: #f5f5f5;
}

input[type="radio"],
input[type="checkbox"] {
  margin-right: 10px;
  cursor: pointer;
}

label {
  cursor: pointer;
  display: inline-block;
  margin-left: 5px;
  vertical-align: middle;
  line-height: 1.4;
}

.submit-btn {
  display: block;
  margin: 30px auto;
  padding: 12px 30px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s;
}

.submit-btn:hover {
  background-color: #45a049;
}

.submit-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}
</style>
