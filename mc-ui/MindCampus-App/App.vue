<script>
import config from './config'
import {getToken} from '@/utils/auth'

export default {
    onLaunch: function() {
      // 添加：全局隐藏系统原生 tabBar，为自定义玻璃胶囊让路
      uni.hideTabBar({
        animation: false
      });
      
      this.initApp()
    },
    methods: {
      initApp() {
        this.initConfig()
        //#ifdef H5
        this.checkLogin()
        //#endif
      },
      initConfig() {
        this.globalData.config = config
      },
      checkLogin() {
        if (!getToken()) {
          this.$tab.reLaunch('/pages/login') 
        }
      }
    }
  }
</script>

<style lang="scss">
  @import '@/static/scss/index.scss'
</style>