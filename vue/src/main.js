//import './assets/main.css'

import App from './App.vue'
import router from './router'
import axios from 'axios'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import { createRulesPlugin } from 'vuetify/labs/rules'
import { VMaskInput } from 'vuetify/labs/VMaskInput'
import { VDateInput } from 'vuetify/labs/VDateInput'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createVuetify } from 'vuetify'

const vuetify = createVuetify({
  theme: {
    defaultTheme: 'dark',
  },
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi,
    },
  },
  components: {
    ...components,
    VMaskInput,
    VDateInput,
  },
  directives: {
    ...directives,
  },
});

// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'


const app = createApp(App);
const pinia = createPinia(App);
pinia.use(piniaPluginPersistedstate);

app.use(pinia);
app.use(router);
app.use(vuetify);
app.use(createRulesPlugin({ /* options */ }, vuetify.locale));
app.provide("$axios", axios);
app.mount('#app');
