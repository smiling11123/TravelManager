<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { UserOutlined, CalendarOutlined, EyeOutlined } from '@ant-design/icons-vue';
import { getArticleList } from '@/api/dashboard/analysis';
import { ArticleVO } from 'types/Entity'; 



const router = useRouter();
const list = ref<ArticleVO[]>([]);

onMounted(async () => {
  const res = await getArticleList() as any;
  if(res.code === 200) {
     list.value = res.data;
  }
});

const gotoDetail = (id: any) => {
  router.push({ name: 'ArticleDetail', params: { id: id } });
};

const formatDate = (dateStr: string | undefined) => {
  if (!dateStr) return '-';
  return dateStr.substring(0, 10);
};
</script>

<template>
  <page-container>
    <div class="mt-2">
      <a-row :gutter="[16, 16]">
        <a-col 
          v-for="(item, index) in list" 
          :key="index" 
          :xs="24" :sm="24" :md="12" :lg="8" :xl="6"
        >
          <a-card
            :bordered="false"
            class="article-card cursor-pointer transition-all duration-300 hover:shadow-lg group"
            @click="gotoDetail(item.id)"
            :bodyStyle="{ padding: '12px' }" 
          >
            <div class="flex flex-col sm:flex-row lg:flex-col h-auto">
              
              <div class="shrink-0 relative overflow-hidden rounded-lg 
                          w-full h-40
                          sm:w-32 sm:h-32 
                          lg:w-full lg:h-40
                          mb-3 sm:mb-0 sm:mr-4 
                          lg:mr-0 lg:mb-3">
                <img 
                  class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110" 
                  :src="item.thumbnail || 'https://via.placeholder.com/300x200'" 
                  alt="cover"
                >
              </div>

              <div class="flex flex-col justify-between flex-1 overflow-hidden min-h-[80px]">
                
                <div>
                  <h3 class="text-base font-bold truncate mb-1 group-hover:text-blue-500 transition-colors">
                    {{ item.title }}
                  </h3>
                  <p class="text-gray-500 text-xs line-clamp-2 leading-relaxed mb-0">
                    {{ item.summary || '暂无摘要' }}
                  </p>
                </div>

                <div class="flex items-center justify-between text-xs text-gray-400 mt-2 sm:mt-0 lg:mt-3">
                  <div class="flex items-center gap-2 overflow-hidden">
                    <span class="flex items-center gap-1 shrink-0">
                      <UserOutlined />
                      <span class="truncate max-w-[60px]">{{ item.auth || '匿名作者' }}</span>
                    </span>
                    <span class="flex items-center gap-1 shrink-0">
                      <CalendarOutlined />
                      {{ formatDate(item.createTime) }}
                    </span>
                  </div>
                  <span class="flex items-center gap-1 shrink-0 ml-1">
                    <EyeOutlined />
                    {{ item.viewCount || 0 }}
                  </span>
                </div>

              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </div>
  </page-container>
</template>

<style scoped>
.article-card {
  border-radius: 12px;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}
</style>
