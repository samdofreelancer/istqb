<template>
  <div class="exam-list">
    <h2>Available Exams</h2>
    <div v-for="exam in exams" :key="exam.id" class="exam-item">
      <h3>{{ exam.title }}</h3>
      <p>{{ exam.description }}</p>
      <p>Time limit: {{ exam.timeLimitSec / 60 }} minutes</p>
      <router-link :to="'/exam/' + exam.id" class="take-exam-btn">Take Exam</router-link>
    </div>
  </div>
</template>

<script>
import { useExamStore } from '../stores/exam'

export default {
  async created() {
    const store = useExamStore()
    await store.fetchExams()
  },
  computed: {
    exams() {
      return useExamStore().exams
    }
  }
}
</script>

<style scoped>
.exam-list {
  padding: 20px;
}
.exam-item {
  border: 1px solid #ddd;
  padding: 15px;
  margin: 10px 0;
  border-radius: 4px;
}
.take-exam-btn {
  display: inline-block;
  padding: 8px 16px;
  background-color: #4CAF50;
  color: white;
  text-decoration: none;
  border-radius: 4px;
}
</style>
