<template>
  <v-container>
    <v-row>
      <!-- Earnings Card -->
      <v-col cols="12" sm="6" md="3">
        <v-card elevation="2">
          <v-card-text>
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="text-caption text-grey">Total Earnings</div>
                <div class="text-h5 font-weight-bold">$12,500</div>
                <!-- <div class="text-caption text-success">+10%</div> -->
              </div>
              <v-icon color="primary" size="x-large">mdi-currency-usd</v-icon>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Tasks Card -->
      <v-col cols="12" sm="6" md="3">
        <v-card elevation="2">
          <v-card-text>
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="text-caption text-grey">Total Task</div>
                <div class="text-h5 font-weight-bold">450</div>
                <!-- <div class="text-caption text-error">-5%</div> -->
              </div>
              <v-icon color="error" size="x-large">mdi-clipboard-list</v-icon>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Views Card -->
      <v-col cols="12" sm="6" md="3">
        <v-card elevation="2">
          <v-card-text>
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="text-caption text-grey">Total Views</div>
                <div class="text-h5 font-weight-bold">255</div>
                <!-- <div class="text-caption text-success">+8%</div> -->
              </div>
              <v-icon color="success" size="x-large">mdi-eye</v-icon>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Downloads Card -->
      <v-col cols="12" sm="6" md="3">
        <v-card elevation="2">
          <v-card-text>
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="text-caption text-grey">Total Downloads</div>
                <div class="text-h5 font-weight-bold">499</div>
                <!-- <div class="text-caption text-warning">+3%</div> -->
              </div>
              <v-icon color="warning" size="x-large">mdi-download</v-icon>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
// You can add reactive data here if needed
</script>

<style scoped>

</style>