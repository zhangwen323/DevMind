import js from '@eslint/js'
import pluginVue from 'eslint-plugin-vue'
import globals from 'globals'

export default [
  // 1. 引入 ESLint 推荐的 JS 规则
  js.configs.recommended,
  
  // 2. 引入 Vue 的 Flat Config 推荐规则
  ...pluginVue.configs['flat/recommended'],
  
  {
    // 指定应用范围
    files: ['src/**/*.js', 'src/**/*.vue', 'vite.config.js'],
    
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      // 3. 核心修改：定义全局变量环境
      globals: {
        ...globals.browser, // 解决 window, document 等未定义错误
        ...globals.node,    // 解决 process (Vite 配置中常用) 未定义错误
        ...globals.es2021   // 启用 ES2021 全局变量
      }
    },
    
    rules: {
      // 保持你原有的规则
      'no-unused-vars': ['error', { argsIgnorePattern: '^_' }],
      
      // 可选：如果你觉得某些 Vue 格式警告太烦，可以在这里关闭或调整
      // 例如：'vue/multi-word-component-names': 'off'
    }
  }
]