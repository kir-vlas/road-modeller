import Vue from 'vue';
import VueResource from 'vue-resource';
import 'lodash';
import index from './register.vue'
import {
    Aside,
    Carousel,
    CarouselItem,
    Checkbox,
    Col,
    Container,
    DatePicker,
    Footer,
    Header,
    Input,
    Main,
    Option,
    Popover,
    Radio,
    RadioButton,
    RadioGroup,
    Row,
    Select,
    Switch,
    Table,
    Upload
} from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

//
Vue.use(Carousel);
Vue.use(CarouselItem);
Vue.use(RadioButton);
Vue.use(Upload);
Vue.use(Footer);
Vue.use(Checkbox);
Vue.use(Popover);
Vue.use(Main);
Vue.use(Switch);
Vue.use(Select);
Vue.use(Option);
Vue.use(Input);
Vue.use(DatePicker);
Vue.use(Radio);
Vue.use(RadioGroup);
Vue.use(Header);
Vue.use(Container);
Vue.use(Main);
Vue.use(Aside);
Vue.use(VueResource);
Vue.use(Col);
Vue.use(Table);
Vue.use(Row);

Vue.component("register", index);

new Vue({
    el: '#main-container'
});