<template>
  <div class="exam-list">
    <h2>Available Exams</h2>
    <div v-for="exam in exams" :key="exam.id" class="exam-item">
      <h3>{{ exam.title }}</h3>
      <p>{{ exam.description }}</p>
      <p>Time limit: {{ exam.timeLimitSec ? `${Math.round(exam.timeLimitSec / 60)} minutes` : 'No time limit' }}</p>
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
  max-width: 800px;
  margin: 0 auto;
}

h2 {
  color: #2c3e50;
  margin-bottom: 20px;
}

.exam-item {
  border: 1px solid #ddd;
  padding: 20px;
  margin: 15px 0;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}

.exam-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.exam-item h3 {
  color: #34495e;
  margin-bottom: 10px;
}

.exam-item p {
  color: #666;
  margin: 8px 0;
  line-height: 1.6;
}

.take-exam-btn {
  display: inline-block;
  margin-top: 15px;
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.take-exam-btn:hover {
  background-color: #45a049;
}
</style>
