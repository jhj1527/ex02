<script setup>
  import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale, PointElement, LineElement } from 'chart.js'
  import { Bar } from 'vue-chartjs'
  import { defineProps } from 'vue';

  ChartJS.register(CategoryScale, LinearScale, BarElement, PointElement, LineElement, Title, Tooltip, Legend);

  const props = defineProps({
    barChartData: {
      type: Object,
      default: () => ({})
    },
    barChartOptions: {
      type: Object,
      default: () => ({})
    }
  });
</script>
<template>
  <Bar :data="props.barChartData" :options="props.barChartOptions" />
</template>
<style>
  
</style>