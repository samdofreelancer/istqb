<template>
  <div class="take-exam" v-if="exam">
    <h2>{{ exam.title }}</h2>
    <p>{{ exam.description }}</p>
    <div v-for="(question, index) in exam.questions" :key="question.id" class="question">
      <h3>Question {{ index + 1 }}</h3>
      <p>{{ question.content }}</p>
      <div class="choices">
        <template v-if="question.multiple">
          <div v-for="choice in question.choices" :key="choice.id">
            <input type="checkbox" 
                   :id="'choice-' + choice.id"
                   v-model="answers[question.id]"
                   :value="choice.id">
            <label :for="'choice-' + choice.id">{{ choice.content }}</label>
          </div>
        </template>
        <template v-else>
          <div v-for="choice in question.choices" :key="choice.id">
            <input type="radio" 
                   :id="'choice-' + choice.id"
                   v-model="answers[question.id]"
                   :value="choice.id"
                   :name="'question-' + question.id">
            <label :for="'choice-' + choice.id">{{ choice.content }}</label>
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
        const attempt = {
          exam: { id: exam.value.id },
          answers: Object.entries(answers.value).map(([questionId, selectedIds]) => ({
            question: { id: parseInt(questionId) },
            selectedChoiceIds: Array.isArray(selectedIds) ? selectedIds : selectedIds ? [selectedIds] : []
          })).filter(answer => answer.selectedChoiceIds.length > 0)
        }
        
        console.log('Submitting attempt:', attempt)
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
}
.question {
  margin: 20px 0;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.choices {
  margin: 10px 0;
}
.submit-btn {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>
