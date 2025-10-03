<template>
  <div class="result-view">
    <h2>Exam Results</h2>
    <div class="score-display">
      <p>Your Score: {{ scoreNum }} / {{ totalNum }}</p>
      <p>Percentage: {{ (scoreNum / totalNum * 100).toFixed(1) }}%</p>
    </div>
    <router-link to="/" class="back-btn">Back to Exams</router-link>
  </div>
</template>

<script>
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
.result-view {
  padding: 20px;
  text-align: center;
}
.score-display {
  margin: 20px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.back-btn {
  display: inline-block;
  padding: 8px 16px;
  background-color: #4CAF50;
  color: white;
  text-decoration: none;
  border-radius: 4px;
}
</style>
