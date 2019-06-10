import Vue from 'vue';
import VueResource from 'vue-resource';
import 'lodash';
import index from './register.vue'
import VueMaterial from 'vue-material'
import 'vue-material/dist/vue-material.min.css'
import 'vue-material/dist/theme/black-green-light.css'

Vue.use(VueMaterial);
Vue.use(VueResource);

Vue.component("register", index);

new Vue({
    el: '#main-container'
});