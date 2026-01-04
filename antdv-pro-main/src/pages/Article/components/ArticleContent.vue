<template>
  <article class="article-content">
    <div class="markdown-body" v-html="htmlContent"></div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import MarkdownIt from 'markdown-it'
import 'github-markdown-css/github-markdown-light.css'
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.css'

const props = defineProps({
  content: {
    type: String,
    default: ''
  }
})

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return '<pre class="hljs"><code>' +
               hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
               '</code></pre>';
      } catch (__) {}
    }
    return '<pre class="hljs"><code>' + md.utils.escapeHtml(str) + '</code></pre>';
  }
})

const htmlContent = computed(() => {
  return md.render(props.content || '')
})
</script>

<style scoped>
/* 1. 容器布局优化 */
.article-content {
  max-width: 920px;
  margin: 0 auto;
  padding: 40px 20px;
  background-color: #ffffff;
  min-height: 50vh;
  border-radius: 4px;
}

/* 2. 全局字体优化 */
:deep(.markdown-body) {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  font-size: 16px;
  line-height: 1.8;
  color: #2c3e50;
}

/* 3. 标题优化 */
:deep(.markdown-body h1),
:deep(.markdown-body h2) {
  border-bottom: none;
  margin-top: 2rem;
  margin-bottom: 1rem;
  font-weight: 700;
}

/* 4. 图片优化 */
:deep(.markdown-body img) {
  max-width: 100%;
  border-radius: 8px;
  display: block;
  margin: 20px auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid #f0f0f0;
}

/* 5. 链接优化 */
:deep(.markdown-body a) {
  color: #409eff;
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: all 0.3s;
}
:deep(.markdown-body a:hover) {
  border-bottom-color: #409eff;
}

/* 6. 引用块优化 */
:deep(.markdown-body blockquote) {
  color: #666;
  padding: 10px 20px;
  background-color: #f8f9fa;
  border-left: 4px solid #42b983;
  border-radius: 4px;
  margin: 20px 0;
}

:deep(.markdown-body pre) {
  position: relative;
  background-color: #282c34; /* 背景色 */
  border-radius: 8px;
  margin: 20px 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  
  /* 核心修改：禁止 pre 本身滚动，把它当做固定的相框 */
  overflow: hidden !important; 
  padding-top: 40px !important; /* 留出顶部空间给红绿灯 */
  padding-left: 0 !important;
  padding-right: 0 !important;
  padding-bottom: 0 !important;
}

/* Mac 风格红绿灯 (绝对定位在 pre 上，所以不会动) */
:deep(.markdown-body pre)::before {
  content: " ";
  position: absolute;
  top: 15px;
  left: 15px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #fc625d; /* 红色 */
  box-shadow: 20px 0 0 #fdbc40, 40px 0 0 #35cd4b; /* 黄色和绿色 */
  z-index: 10;
}

/* 内层代码：负责滚动 */
:deep(.markdown-body pre code) {
  display: block;        /* 必须变为块级元素才能滚动 */
  overflow-x: auto;      /* 水平滚动条出现在这里 */
  padding: 0 15px 15px;  /* 内部留白 */
  font-family: 'JetBrains Mono', 'Fira Code', Consolas, monospace;
  font-size: 14px;
  line-height: 1.5;
  color: #abb2bf;        /* 确保文字颜色对比度 */
  width: 100%;           /* 撑满容器 */
  box-sizing: border-box;
}

/* 8. 自定义滚动条 (作用于 code 标签) */
:deep(.markdown-body pre code::-webkit-scrollbar) {
  height: 8px; /* 滚动条高度 */
}
:deep(.markdown-body pre code::-webkit-scrollbar-thumb) {
  background-color: #4b5263; /* 滚动滑块颜色 */
  border-radius: 4px;
}
:deep(.markdown-body pre code::-webkit-scrollbar-track) {
  background-color: transparent; /* 轨道透明 */
}
</style>
