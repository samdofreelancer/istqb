<template>
  <div v-if="isOpen" class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity z-40">
    <div class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex min-h-full items-end justify-center p-4 text-center sm:items-center sm:p-0">
        <div class="relative transform overflow-hidden rounded-lg bg-white text-left shadow-xl transition-all sm:my-8 sm:w-full sm:max-w-3xl">
          <!-- Header -->
          <div class="bg-white px-4 pb-4 pt-5 sm:p-6 sm:pb-4">
            <div class="flex items-start justify-between">
              <h3 class="text-xl font-semibold leading-6 text-gray-900">{{ exam.title }}</h3>
              <button 
                @click="close"
                class="rounded-md bg-white text-gray-400 hover:text-gray-500 focus:ring-2 focus:ring-green-500"
              >
                <span class="material-icons">close</span>
              </button>
            </div>
            <p class="mt-2 text-sm text-gray-500">{{ exam.description }}</p>
          </div>

          <!-- Questions List -->
          <div class="px-4 pb-4 sm:px-6 max-h-[60vh] overflow-y-auto">
            <div v-for="(question, index) in exam.questions" :key="index" class="mb-6 last:mb-0">
              <div class="flex items-center gap-2 mb-2">
                <span class="inline-flex items-center justify-center w-6 h-6 rounded-full bg-gray-100 text-sm font-medium text-gray-700">{{ index + 1 }}</span>
                <h4 class="text-base font-medium text-gray-900">{{ question.text }}</h4>
              </div>
              <div class="ml-8 space-y-2">
                <div 
                  v-for="choice in question.choices" 
                  :key="choice.id"
                  :class="[
                    'flex items-start p-3 rounded-lg border',
                    choice.correct 
                      ? 'border-green-200 bg-green-50' 
                      : 'border-gray-200 bg-gray-50'
                  ]"
                >
                  <span 
                    class="material-icons text-base mr-2"
                    :class="choice.correct ? 'text-green-600' : 'text-gray-400'"
                  >
                    {{ choice.correct ? 'check_circle' : 'radio_button_unchecked' }}
                  </span>
                  <span :class="choice.correct ? 'font-medium' : ''">{{ choice.text }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Footer -->
          <div class="bg-gray-50 px-4 py-3 sm:flex sm:flex-row-reverse sm:px-6">
            <button
              type="button"
              class="mt-3 inline-flex w-full justify-center rounded-xl bg-white px-3 py-2 text-sm font-semibold text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 hover:bg-gray-50 sm:mt-0 sm:w-auto"
              @click="close"
            >
              Close Preview
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Choice {
  id: string;
  text: string;
  correct: boolean;
}

interface Question {
  id: string;
  text: string;
  choices: Choice[];
}

interface Exam {
  id: string;
  title: string;
  description: string;
  questions: Question[];
}

const props = defineProps<{
  isOpen: boolean;
  exam: Exam;
}>();

const emit = defineEmits<{
  (e: 'close'): void;
}>();

const close = () => {
  emit('close');
};
</script>
