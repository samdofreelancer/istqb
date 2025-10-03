<template>
  <div class="import-exam">
    <h2>Import Exam</h2>
    <div class="import-sections">
      <div class="paste-section">
        <h3>Paste JSON</h3>
        <textarea 
          v-model="jsonData" 
          placeholder="Paste JSON here"
          @drop.prevent="handleDrop"
          @dragover.prevent
        ></textarea>
      </div>
      <div class="upload-section">
        <h3>Upload JSON File</h3>
        <div 
          class="upload-area"
          @drop.prevent="handleDrop"
          @dragover.prevent
          @dragenter.prevent
          @dragleave.prevent
          :class="{ 'drag-over': isDragging }"
        >
          <input 
            type="file"
            ref="fileInput"
            accept=".json"
            @change="handleFileUpload"
            style="position: absolute; opacity: 0; cursor: pointer; width: 100%; height: 100%;"
          />
          <div class="upload-content">
            <button type="button" class="choose-file">Choose File</button>
            <span class="file-name">{{ fileName || 'No file chosen' }}</span>
            <div class="drag-text">or drag and drop a file here</div>
          </div>
        </div>
      </div>
    </div>
    <div class="action-buttons">
      <button 
        class="import-button" 
        @click="importExam" 
        :disabled="!hasContent"
      >
        Import
      </button>
      <button 
        v-if="hasContent" 
        class="clear-button"
        @click="clearContent"
      >
        Clear
      </button>
    </div>
  </div>
</template>

<script>
import { useExamStore } from '../stores/exam'

export default {
  data() {
    return {
      jsonData: '',
      fileName: '',
      isDragging: false
    }
  },
  computed: {
    hasContent() {
      return this.jsonData.trim().length > 0
    }
  },
  methods: {
    async importExam() {
      try {
        // Validate JSON first
        const examData = JSON.parse(this.jsonData)
        
        // Validate required fields
        if (!examData.title || !examData.questions || !Array.isArray(examData.questions) || examData.questions.length === 0) {
          throw new Error('Invalid exam format. Required fields: title and questions array')
        }
        
        // Validate each question
        examData.questions.forEach((q, index) => {
          if (!q.text || !q.choices || !Array.isArray(q.choices) || q.choices.length === 0) {
            throw new Error(`Invalid question format at index ${index}. Required fields: text and choices array`)
          }
          q.choices.forEach((c, cIndex) => {
            if (typeof c.text === 'undefined' || typeof c.correct === 'undefined') {
              throw new Error(`Invalid choice format at question ${index}, choice ${cIndex}. Required fields: text and correct`)
            }
          })
        })
        
        const store = useExamStore()
        await store.importExam(examData)
        this.$router.push('/')
      } catch (e) {
        if (e.name === 'SyntaxError') {
          alert('Invalid JSON format. Please check your input.')
        } else if (e.response?.status === 415) {
          alert('Server error: Unsupported Media Type. Please ensure you are sending valid JSON.')
        } else {
          alert(e.message || 'Import failed. Please try again.')
        }
        console.error('Import error:', e)
      }
    },
    handleFileUpload(event) {
      const file = event.target.files[0]
      if (file) {
        if (!file.name.endsWith('.json')) {
          alert('Please select a JSON file')
          event.target.value = '' // Clear the file input
          return
        }
        this.readFile(file)
      }
    },
    handleDrop(event) {
      this.isDragging = false
      const file = event.dataTransfer.files[0]
      if (file) {
        if (!file.name.endsWith('.json')) {
          alert('Please drop a JSON file')
          return
        }
        this.readFile(file)
      }
    },
    readFile(file) {
      const reader = new FileReader()
      reader.onload = (e) => {
        try {
          JSON.parse(e.target.result)
          this.jsonData = e.target.result
          this.fileName = file.name
        } catch (error) {
          alert('Invalid JSON file')
          this.fileName = ''
        }
      }
      reader.onerror = () => {
        alert('Error reading file')
        this.fileName = ''
      }
      reader.readAsText(file)
    },
    clearContent() {
      this.jsonData = ''
      this.fileName = ''
      if (this.$refs.fileInput) {
        this.$refs.fileInput.value = ''
      }
    }
  }
}
</script>

<style scoped>
.import-exam {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.import-sections {
  display: flex;
  gap: 20px;
  margin: 20px 0;
}

.paste-section, .upload-section {
  flex: 1;
}

h2 {
  color: #2c3e50;
  margin-bottom: 20px;
}

h3 {
  margin-bottom: 10px;
  color: #34495e;
  font-size: 1.2em;
}

textarea {
  width: 100%;
  height: 200px;
  padding: 12px;
  border: 2px dashed #ccc;
  border-radius: 8px;
  font-family: monospace;
  resize: vertical;
  font-size: 14px;
}

textarea:focus {
  outline: none;
  border-color: #4CAF50;
}

.upload-area {
  position: relative;
  height: 200px;
  border: 2px dashed #ccc;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.upload-area:hover, .upload-area.drag-over {
  border-color: #4CAF50;
  background-color: #f8f8f8;
}

.upload-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  pointer-events: none;
}

.choose-file {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.file-name {
  color: #666;
  font-size: 14px;
}

.drag-text {
  color: #666;
  font-size: 14px;
  margin-top: 8px;
}

.action-buttons {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.import-button {
  flex: 2;
  padding: 12px 24px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease;
}

.clear-button {
  flex: 1;
  padding: 12px 24px;
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: background-color 0.3s ease;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.import-button:hover:not(:disabled) {
  background-color: #45a049;
}

.clear-button:hover {
  background-color: #da190b;
}
</style>
