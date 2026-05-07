<script setup lang="ts">
import { RouterView } from 'vue-router'
import { NMessageProvider, NDialogProvider } from 'naive-ui'
</script>

<template>
  <n-message-provider>
    <n-dialog-provider>
      <RouterView />
    </n-dialog-provider>
  </n-message-provider>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app {
  width: 100%;
  height: 100%;
}

@media print {
  * {
    overflow: visible !important;
  }

  html, body, #app {
    height: auto !important;
    min-height: auto !important;
    overflow: visible !important;
    margin: 0 !important;
    padding: 0 !important;
  }

  body {
    margin: 0 !important;
    padding: 0 !important;
  }

  @page {
    margin: 10mm;
    size: A4 landscape;
  }
}
</style>

