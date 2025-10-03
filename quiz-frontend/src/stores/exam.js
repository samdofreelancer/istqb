import { defineStore } from 'pinia'
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8081/api'
})

export const useExamStore = defineStore('exam', {
  state: () => ({
    exams: [],
    currentExam: null
  }),
  
  actions: {
    async importExam(examData) {
      const response = await api.post('/exams/import', examData)
      return response.data
    },
    
    async fetchExams() {
      const response = await api.get('/exams')
      this.exams = response.data
      return this.exams
    },
    
    async fetchExamById(id) {
      const response = await api.get(`/exams/${id}`)
      this.currentExam = response.data
      return this.currentExam
    },
    
    async submitAttempt(attempt) {
      const response = await api.post('/attempts/submit', attempt)
      return response.data
    }
  }
})
