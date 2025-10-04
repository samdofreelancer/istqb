<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <!-- Header -->
    <div class="mb-6">
      <h1 class="text-2xl font-semibold">Import Exam</h1>
      <nav class="text-sm text-gray-600 mt-1">
        <span>Exams</span> | <span>Import Exam</span>
      </nav>
    </div>

    <!-- Import Cards -->
    <div class="grid gap-5 md:grid-cols-2 mb-6">
      <!-- Paste JSON Card -->
      <div class="rounded-2xl border border-gray-200 bg-white shadow-sm p-4">
        <label for="paste-textarea" class="text-base font-semibold mb-1 block">Paste JSON</label>
        <p class="text-sm text-gray-600 mb-3">Paste your exam JSON data here</p>
        <textarea
          id="paste-textarea"
          v-model="rawText"
          placeholder="Paste JSON here"
          class="min-h-[220px] w-full rounded-xl border border-dashed border-gray-300 px-3 py-2 focus:ring-2 focus:ring-green-500 focus:border-transparent resize-none overflow-auto"
          @input="handleTextInput"
          :class="{ 'border-red-300': errors.length > 0 }"
        ></textarea>
        <div v-if="errors.length > 0" class="mt-2 text-sm text-red-600" aria-live="polite">
          {{ errors[0] }}
        </div>
      </div>

      <!-- Upload JSON File Card -->
      <div class="rounded-2xl border border-gray-200 bg-white shadow-sm p-4">
        <label for="file-input" class="text-base font-semibold mb-1 block">Upload JSON File</label>
        <p class="text-sm text-gray-600 mb-3">Upload a .json file containing exam data</p>
        <div
          class="group relative flex flex-col items-center justify-center min-h-[220px] rounded-xl border-2 border-dashed border-gray-300 hover:border-gray-400 transition"
          :class="{ 'border-green-500 bg-green-50': isDragging }"
          @dragover.prevent="isDragging = true"
          @dragleave.prevent="isDragging = false"
          @drop.prevent="handleDrop"
          @keydown.enter.prevent="triggerFileInput"
          @keydown.space.prevent="triggerFileInput"
          role="button"
          tabindex="0"
          aria-label="Upload JSON file"
        >
          <input
            id="file-input"
            type="file"
            accept=".json"
            @change="handleFileChange"
            class="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
            ref="fileInputRef"
          />
          <button
            type="button"
            class="bg-green-600 text-white rounded-xl px-3 py-2 hover:bg-green-700 focus:ring-2 focus:ring-green-500"
            @click.stop="triggerFileInput"
          >
            Choose File
          </button>
          <span class="text-sm text-gray-600 mt-2">
            {{ fileName || 'No file chosen' }}
          </span>
          <div class="text-sm text-gray-500 mt-1">
            or drag and drop a .json file here
          </div>
        </div>
        <div v-if="errors.length > 0 && lastSource === 'upload'" class="mt-2 text-sm text-red-600" aria-live="polite">
          {{ errors[0] }}
        </div>
      </div>
    </div>

    <!-- Preview Card -->
    <div v-if="valid && parsed" class="rounded-2xl border bg-white shadow-sm p-4 mt-6">
      <h2 class="text-lg font-semibold mb-3">Preview</h2>
      <div class="mb-4">
        <h3 class="text-xl font-medium">{{ parsed.title }}</h3>
        <p v-if="parsed.description" class="text-gray-600 mt-1 line-clamp-2">{{ parsed.description }}</p>
      </div>
      <div class="flex gap-4 mb-4">
        <span class="bg-blue-100 text-blue-800 px-2 py-1 rounded text-sm">
          #️⃣ {{ parsed.questions.length }} questions
        </span>
        <span class="bg-yellow-100 text-yellow-800 px-2 py-1 rounded text-sm">
          ⏱ {{ parsed.timeLimitSec ? Math.floor(parsed.timeLimitSec / 60) + ' mins' : 'No time limit' }}
        </span>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full text-sm">
          <thead>
            <tr class="border-b">
              <th class="text-left py-2">#</th>
              <th class="text-left py-2">Question</th>
              <th class="text-left py-2">Options</th>
              <th class="text-left py-2">Type</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(q, idx) in parsed.questions.slice(0, 5)" :key="idx" class="border-b">
              <td class="py-2">{{ idx + 1 }}</td>
              <td class="py-2">{{ q.text.length > 40 ? q.text.substring(0, 40) + '...' : q.text }}</td>
              <td class="py-2">{{ q.choices.length }}</td>
              <td class="py-2">{{ q.multiple ? 'Multiple' : 'Single' }}</td>
            </tr>
          </tbody>
        </table>
        <p v-if="parsed.questions.length > 5" class="text-sm text-gray-500 mt-2">
          ... and {{ parsed.questions.length - 5 }} more questions
        </p>
      </div>
    </div>

    <!-- Import Button -->
    <div class="mt-6 flex justify-center">
      <button
        @click="handleImport"
        :disabled="!valid || isImporting"
        class="w-full md:w-auto rounded-xl px-5 py-2.5 bg-green-600 text-white hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed focus:ring-2 focus:ring-green-500"
      >
        <span v-if="isImporting" class="inline-flex items-center">
          <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          Importing...
        </span>
        <span v-else>Import</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useExamStore } from '../stores/exam';
import { validateExam, type NormalizedExam } from '../utils/validateExam';
import { useRouter } from 'vue-router';

const MAX_BYTES = 2 * 1024 * 1024; // 2MB

// State
const rawText = ref('');
const fileName = ref('');
const parsed = ref<NormalizedExam | null>(null);
const valid = ref(false);
const errors = ref<string[]>([]);
const warnings = ref<string[]>([]);
const isDragging = ref(false);
const isImporting = ref(false);
const lastSource = ref<'paste' | 'upload' | null>(null);

const fileInputRef = ref<HTMLInputElement | null>(null);

// Emits
const emit = defineEmits<{
  imported: [payload: NormalizedExam & { source: 'paste' | 'upload' }];
  error: [errors: string[]];
  cancel: [];
}>();

// Methods
function parseAndValidate(text: string, source: 'paste' | 'upload') {
  const result = validateExam(text);
  errors.value = result.errs;
  warnings.value = result.warns;
  parsed.value = result.normalized;
  valid.value = result.ok;
  lastSource.value = source;
}

function handleTextInput() {
  if (rawText.value.trim()) {
    parseAndValidate(rawText.value, 'paste');
  } else {
    parsed.value = null;
    valid.value = false;
    errors.value = [];
    warnings.value = [];
  }
}

function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (file) {
    readFile(file);
  }
}

function handleDrop(event: DragEvent) {
  isDragging.value = false;
  const file = event.dataTransfer?.files?.[0];
  if (!file) return;

  if (!file.name.endsWith('.json')) {
    errors.value = ['Only .json files are allowed'];
    return;
  }
  if (file.size > MAX_BYTES) {
    errors.value = ['File too large (max 2MB)'];
    return;
  }

  readFile(file);
}

function readFile(file: File) {
  const reader = new FileReader();
  reader.onload = (e) => {
    const text = e.target?.result as string;
    rawText.value = text;
    fileName.value = file.name;
    parseAndValidate(text, 'upload');
  };
  reader.onerror = () => {
    errors.value = ['Error reading file'];
  };
  reader.readAsText(file);
}

function triggerFileInput() {
  fileInputRef.value?.click();
}

const router = useRouter();

async function handleImport() {
  if (!valid.value || !parsed.value) return;

  isImporting.value = true;
  try {
    const store = useExamStore();
    await store.importExam(parsed.value);
    emit('imported', { ...parsed.value, source: lastSource.value! });
    // Navigate to the exam list page
    router.push('/');
  } catch (error: any) {
    emit('error', [error.message || 'Import failed']);
  } finally {
    isImporting.value = false;
  }
}

// Watch for text changes
watch(rawText, handleTextInput);
</script>

<style scoped>
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
