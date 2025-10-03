import { createRouter, createWebHistory } from 'vue-router'
import ExamList from '../views/ExamList.vue'
import ImportExam from '../views/ImportExam.vue'
import TakeExam from '../views/TakeExam.vue'
import ResultView from '../views/ResultView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: ExamList
    },
    {
      path: '/import',
      name: 'import',
      component: ImportExam
    },
    {
      path: '/exam/:id',
      name: 'exam',
      component: TakeExam
    },
    {
      path: '/result/:score/:total',
      name: 'result',
      component: ResultView,
      props: true
    }
  ]
})

export default router
