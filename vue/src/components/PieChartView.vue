<script setup>
  import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
  import { Pie } from 'vue-chartjs'
  import { Doughnut } from 'vue-chartjs'
  import { defineProps } from 'vue'

  ChartJS.register(ArcElement, Tooltip, Legend);

  const props = defineProps({
    pieChartData: {
      type: Object,
      default: () => ({})
    },
    pieChartOptions: {
      type: Object,
      default: () => ({})
    }
  });
</script>
<template>
  <Doughnut :data="props.pieChartData" :options="props.pieChartOptions" />
</template>
<style>
  
</style>