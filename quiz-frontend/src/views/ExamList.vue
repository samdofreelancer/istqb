<template>
  <div class="max-w-5xl mx-auto px-4 py-6">
    <!-- Header -->
    <div class="flex justify-between items-center mb-8">
      <h1 class="text-2xl font-bold text-gray-900">Exams</h1>
      <router-link 
        to="/import" 
        class="inline-flex items-center px-4 py-2 bg-green-600 text-white font-medium rounded-lg hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 transition-colors"
      >
        <span class="material-icons mr-2 text-sm">upload_file</span>
        Import Exam
      </router-link>
    </div>

    <!-- Toolbar -->
    <div class="flex flex-col sm:flex-row gap-4 mb-6">
      <div class="relative flex-1">
        <span class="absolute inset-y-0 left-0 pl-3 flex items-center">
          <span class="material-icons text-gray-400 text-sm">search</span>
        </span>
        <input
          v-model="searchQuery"
          type="text"
          placeholder="Search exams..."
          class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500"
        >
      </div>
      <div class="flex gap-4">
        <select
          v-model="sortBy"
          class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500"
        >
          <option value="title">Sort: Title</option>
          <option value="recent">Sort: Recent</option>
        </select>
        <select
          v-model="statusFilter"
          class="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-green-500"
        >
          <option value="all">All Status</option>
          <option value="not-started">Not Started</option>
          <option value="in-progress">In Progress</option>
          <option value="completed">Completed</option>
        </select>
      </div>
    </div>

    <!-- Empty State -->
    <div v-if="!filteredExams.length" class="mt-8">
      <div class="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center">
        <span class="material-icons text-4xl text-gray-400 mb-3">quiz</span>
        <h3 class="text-lg font-medium text-gray-900 mb-2">No exams found</h3>
        <p class="text-gray-500 mb-4">Try adjusting filters or import a new exam.</p>
        <router-link
          to="/import"
          class="inline-flex items-center px-4 py-2 bg-green-600 text-white font-medium rounded-lg hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 transition-colors"
        >
          Import Exam
        </router-link>
      </div>
    </div>

    <!-- Exam Grid -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 gap-6">
      <div
        v-for="exam in filteredExams"
        :key="exam.id"
        class="bg-white rounded-lg border border-gray-200 shadow-sm hover:shadow-md transition-shadow"
      >
        <div class="p-6">
          <div class="flex items-start justify-between mb-4">
            <h3 class="text-lg font-bold text-gray-900 line-clamp-1">{{ exam.title }}</h3>
            <span 
              :class="[
                'px-2 py-1 text-xs font-medium rounded-full',
                {
                  'bg-gray-100 text-gray-800': !exam.status,
                  'bg-yellow-100 text-yellow-800': exam.status === 'in-progress',
                  'bg-green-100 text-green-800': exam.status === 'completed'
                }
              ]"
            >
              {{ exam.status || 'Not Started' }}
            </span>
          </div>

          <p class="text-gray-600 mb-4 line-clamp-2">{{ exam.description }}</p>

          <div class="flex flex-wrap gap-2 mb-4">
            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
              {{ exam.questions?.length || 0 }} questions
            </span>
            <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
              {{ exam.timeLimitSec ? `${Math.round(exam.timeLimitSec / 60)} mins` : 'No time limit' }}
            </span>
          </div>

          <!-- Progress Bar -->
          <div v-if="exam.progress" class="w-full bg-gray-200 rounded-full h-2 mb-4">
            <div
              class="bg-green-600 h-2 rounded-full"
              :style="{ width: `${exam.progress}%` }"
            ></div>
          </div>

          <div class="flex flex-wrap gap-3">
            <router-link
              :to="'/exam/' + exam.id"
              class="flex-1 inline-flex justify-center items-center px-4 py-2 bg-green-600 text-white font-medium rounded-lg hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 transition-colors"
            >
              <span class="material-icons mr-2 text-sm">
                {{ !exam.status ? 'play_arrow' : exam.status === 'completed' ? 'refresh' : 'resume' }}
              </span>
              {{ !exam.status ? 'Take Exam' : exam.status === 'completed' ? 'Review' : 'Resume' }}
            </router-link>
            <button
              @click="previewExam(exam)"
              class="px-4 py-2 border border-gray-300 text-gray-700 font-medium rounded-lg hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 transition-colors"
            >
              Preview
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <!-- Preview Dialog -->
  <PreviewDialog
    v-if="currentPreviewExam"
    :is-open="isPreviewOpen"
    :exam="currentPreviewExam"
    @close="closePreview"
  />
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useExamStore } from '../stores/exam'
import PreviewDialog from '../components/PreviewDialog.vue'

const examStore = useExamStore()
const searchQuery = ref('')
const sortBy = ref('title')
const statusFilter = ref('all')

onMounted(async () => {
  await examStore.fetchExams()
})

const filteredExams = computed(() => {
  let result = examStore.exams

  // Search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(exam => 
      exam.title.toLowerCase().includes(query) ||
      exam.description.toLowerCase().includes(query)
    )
  }

  // Status filter
  if (statusFilter.value !== 'all') {
    result = result.filter(exam => exam.status === statusFilter.value)
  }

  // Sort
  return result.sort((a, b) => {
    if (sortBy.value === 'title') {
      return a.title.localeCompare(b.title)
    } else {
      // Sort by recent (assuming there's a createdAt field, fallback to id)
      return (b.createdAt || b.id) - (a.createdAt || a.id)
    }
  })
})

const isPreviewOpen = ref(false)
const currentPreviewExam = ref(null)

const previewExam = async (exam) => {
  try {
    const examDetails = await examStore.previewExam(exam.id)
    currentPreviewExam.value = examDetails
    isPreviewOpen.value = true
  } catch (error) {
    console.error('Failed to preview exam:', error)
  }
}

const closePreview = () => {
  isPreviewOpen.value = false
  currentPreviewExam.value = null
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
