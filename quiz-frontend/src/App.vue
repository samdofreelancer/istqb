<template>
  <nav>
    <router-link to="/">Exams</router-link> |
    <router-link to="/import">Import Exam</router-link>
  </nav>
  <router-view></router-view>
</template>

<style>
nav {
  padding: 20px;
}
nav a {
  margin: 0 10px;
}
</style>
