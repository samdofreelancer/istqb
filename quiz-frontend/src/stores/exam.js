import { defineStore } from 'pinia'
import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8081/api',
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

export const useExamStore = defineStore('exam', {
  state: () => ({
    exams: [],
    currentExam: null,
    error: null,
    userAnswers: [],
    examResult: null,
    currentAttempt: null
  }),
  
  actions: {
    async importExam(examData) {
      try {
        const response = await api.post('/exams/import', examData)
        this.error = null
        return response.data
      } catch (error) {
        console.error('Import error:', error)
        this.error = error.response?.data?.message || 'Import failed'
        throw error
      }
    },
    
    async fetchExams() {
      try {
        const response = await api.get('/exams')
        this.exams = response.data
        this.error = null
        return this.exams
      } catch (error) {
        console.error('Fetch error:', error)
        this.error = error.response?.data?.message || 'Failed to fetch exams'
        throw error
      }
    },
    
    async fetchExamById(id) {
      try {
        const response = await api.get(`/exams/${id}`)
        this.currentExam = response.data
        this.error = null
        return this.currentExam
      } catch (error) {
        console.error('Fetch error:', error)
        this.error = error.response?.data?.message || 'Failed to fetch exam'
        throw error
      }
    },

    async previewExam(id) {
      try {
        const response = await api.get(`/exams/${id}`)
        return response.data
      } catch (error) {
        console.error('Preview error:', error)
        this.error = error.response?.data?.message || 'Failed to preview exam'
        throw error
      }
    },

    async deleteExam(id) {
      try {
        await api.delete(`/exams/${id}`)
        // Remove the exam from the local state
        this.exams = this.exams.filter(exam => exam.id !== id)
        this.error = null
      } catch (error) {
        console.error('Delete error:', error)
        this.error = error.response?.data?.message || 'Failed to delete exam'
        throw error
      }
    },
    
    async submitAttempt(attempt) {
      try {
        // Store the current attempt
        this.currentAttempt = attempt
        // Note: User answers are already stored in the correct format by TakeExam.vue
        
        const response = await api.post('/attempts/submit', attempt)
        this.examResult = response.data
        this.error = null
        return response.data
      } catch (error) {
        console.error('Submit error:', error)
        this.error = error.response?.data?.message || 'Failed to submit attempt'
        throw error
      }
    },

    clearExamState() {
      this.currentExam = null
      this.userAnswers = []
      this.examResult = null
    }
  }
})
