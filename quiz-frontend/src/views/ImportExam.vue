<template>
  <div class="import-exam">
    <h2>Import Exam</h2>
    <textarea v-model="jsonData" rows="10" cols="50" placeholder="Paste JSON here"></textarea>
    <br>
    <button @click="importExam">Import</button>
  </div>
</template>

<script>
import { useExamStore } from '../stores/exam'

export default {
  data() {
    return {
      jsonData: ''
    }
  },
  methods: {
    async importExam() {
      try {
        const examData = JSON.parse(this.jsonData)
        const store = useExamStore()
        await store.importExam(examData)
        this.$router.push('/')
      } catch (e) {
        alert('Invalid JSON or import failed')
      }
    }
  }
}
</script>

<style scoped>
.import-exam {
  padding: 20px;
}
textarea {
  margin: 10px 0;
}
</style>
