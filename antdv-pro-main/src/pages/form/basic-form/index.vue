<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

// 引入 Markdown 编辑器
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

// 引入你之前的 API (假设路径如下)
import { publishArticle, updateArticle } from '@/api/Article/ArticleEdit'
import { uploadFile } from '@/api/upload' // 假设你有这个上传文件的API
import { getArticleDetail } from '~@/api/Article/ArticleDetail'

const route = useRoute()
const router = useRouter()

// --- 1. 定义表单数据模型 ---
const formState = reactive({
  id: undefined as number | undefined,
  title: '',
  summary: '',
  content: '', // Markdown 内容
  thumbnail: '',
  categoryId: undefined as number | undefined, // 这里对应省份ID，或者你可以改成 provinceName
  categoryName: '',
  provinceName: '', // 如果你直接存省份名
  name: '', // 景点名称
  latitude: undefined as number | undefined,
  longitude: undefined as number | undefined,
  isTop: false, // 是否置顶
  isComment: true, // 是否允许评论
})

const loading = ref(false)
const mapContainer = ref<HTMLElement>()
let map: L.Map | null = null
let currentMarker: L.Marker | null = null

// --- 2. 生命周期逻辑 ---
onMounted(async () => {
  // 初始化地图
  initMap()

  // 判断是编辑还是新增
  const id = route.params.id
  console.log(id)
  if (id) {
    await loadData(id)
  }
})

// --- 3. 加载回显数据 (编辑模式) ---
const loadData = async (id: any) => {
  loading.value = true
  try {
    const res = await getArticleDetail(id) as any
    if (res.code === 200) {
      const data = res.data
      // 数据回填
      formState.id = data.id
      formState.title = data.title
      formState.summary = data.summary
      formState.content = data.content // Markdown 源码
      formState.thumbnail = data.thumbnail
      formState.name = data.name
      formState.latitude = data.latitude
      formState.longitude = data.longitude
      formState.isTop = data.isTop == 1
      formState.isComment = data.isComment == 1
      formState.categoryName = data.categoryName

      // 核心：如果回显数据有坐标，地图上要画出来
      if (data.latitude && data.longitude) {
        updateMapMarker(data.latitude, data.longitude)
        map?.setView([data.latitude, data.longitude], 13)
      }
    }
  } finally {
    loading.value = false
  }
}

// --- 4. 地图逻辑 ---
const initMap = () => {
  if (!mapContainer.value) return

  map = L.map(mapContainer.value).setView([39.9042, 116.4074], 11) // 默认北京

  L.tileLayer('http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}', {
    maxZoom: 18,
    attribution: 'Gaode'
  }).addTo(map)

  // 地图点击事件：拾取坐标
  map.on('click', (e) => {
    const { lat, lng } = e.latlng
    // 1. 更新表单
    formState.latitude = parseFloat(lat.toFixed(6))
    formState.longitude = parseFloat(lng.toFixed(6))
    
    // 2. 更新地图标记
    updateMapMarker(lat, lng)
    message.success(`已选中坐标: [${formState.latitude}, ${formState.longitude}]`)
  })
}

const updateMapMarker = (lat: number, lng: number) => {
  if (!map) return
  // 清除旧标记
  if (currentMarker) {
    map.removeLayer(currentMarker)
  }
  // 添加新标记
  const customIcon = L.icon({
    iconUrl: 'https://cdn-icons-png.flaticon.com/512/252/252025.png', // 红色图钉
    iconSize: [32, 32],
    iconAnchor: [16, 32]
  })
  currentMarker = L.marker([lat, lng], { icon: customIcon }).addTo(map)
}

// --- 5. 编辑器图片上传逻辑 ---
const onUploadImg = async (files: File[], callback: (urls: string[]) => void) => {
  const res = await Promise.all(
    files.map((file) => {
      return uploadFile(file) // 调用你的上传接口
    })
  )
  
  // 提取 URL 并回调给编辑器，编辑器会自动插入 Markdown 图片语法
  // 假设 uploadFile 返回 { code: 200, data: 'http://...' }
  const urls = res.map((r: any) => r.data)
  callback(urls)
}

// --- 6. 封面上传逻辑 ---
const customUpload = async ({ file, onSuccess }: any) => {
  const res = await uploadFile(file) as any
  if (res.code === 200) {
    formState.thumbnail = res.data
    onSuccess()
  }
}

// --- 7. 提交保存 ---
const handleSubmit = async () => {
  if (!formState.title || !formState.content) {
    return message.warning('标题和内容不能为空')
  }
  if (!formState.latitude) {
    return message.warning('请在地图上点击选择景点位置')
  }

  loading.value = true
  try {
    // 构造参数
    const params = {
      ...formState,
      // Boolean 转后端需要的 0/1
      isTop: formState.isTop ? 1 : 0,
      isComment: formState.isComment ? 1 : 0
    }

    if (formState.id) {
      await updateArticle(params)
      message.success('修改成功')
    } else {
      await publishArticle(params)
      message.success('发布成功')
    }
    
    // 返回列表页
    router.push({ name: 'ArticleList' }) // 确保你有这个路由名
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="publish-container">
    <a-card :title="formState.id ? '编辑文章' : '发布新文章'" :bordered="false">
      <a-form layout="vertical" :model="formState">
        
        <a-row :gutter="24">
          <a-col :span="16">
            <a-form-item label="文章标题" required>
              <a-input v-model:value="formState.title" placeholder="请输入吸引人的标题" size="large" />
            </a-form-item>

            <a-form-item label="文章内容" required>
              <div class="editor-wrapper">
                <MdEditor 
                  v-model="formState.content" 
                  :toolbarsExclude="['github']"
                  placeholder="开始编写你的游记..."
                  @onUploadImg="onUploadImg"
                  style="height: 1000px;"
                />
              </div>
            </a-form-item>
          </a-col>

          <a-col :span="8">
            <a-card title="基础信息" class="mb-4" size="small">
              <a-form-item label="景点名称" required>
                <a-input v-model:value="formState.name" placeholder="例如：故宫博物院" />
              </a-form-item>

              <a-form-item label="所属省份">
                <a-input v-model:value="formState.categoryName" placeholder="请输入省份，例如：北京" />
              </a-form-item>

              <a-form-item label="文章摘要">
                <a-textarea v-model:value="formState.summary" :rows="3" placeholder="简短介绍..." show-count :maxlength="200" />
              </a-form-item>
              
              <a-form-item label="封面图">
                <a-upload
                  list-type="picture-card"
                  class="cover-uploader"
                  :show-upload-list="false"
                  :custom-request="customUpload"
                >
                  <img v-if="formState.thumbnail" :src="formState.thumbnail" alt="avatar" />
                  <div v-else>
                    <span class="upload-text">点击上传</span>
                  </div>
                </a-upload>
              </a-form-item>
            </a-card>
            <a-card title="地理位置 (点击地图选择)" class="mb-4" size="small">
                <div class="location-display mb-2">
                    <a-tag color="blue" v-if="formState.latitude">
                        Lat: {{ formState.latitude }}
                    </a-tag>
                    <a-tag color="green" v-if="formState.longitude">
                        Lng: {{ formState.longitude }}
                    </a-tag>
                    <span v-else class="text-gray-400 text-xs">暂未选择位置</span>
                </div>
                <div ref="mapContainer" class="map-box"></div>
            </a-card>

            <a-card title="发布设置" size="small">
                <div class="setting-item">
                    <span>允许评论</span>
                    <a-switch v-model:checked="formState.isComment" />
                </div>
                <a-divider style="margin: 12px 0" />
                <div class="setting-item">
                    <span>是否置顶</span>
                    <a-switch v-model:checked="formState.isTop" />
                </div>
                
                <div class="mt-6">
                    <a-button type="primary" block size="large" :loading="loading" @click="handleSubmit">
                        {{ formState.id ? '确认修改' : '立即发布' }}
                    </a-button>
                </div>
            </a-card>

          </a-col>
        </a-row>

      </a-form>
    </a-card>
  </div>
</template>


<style scoped lang="scss">
.publish-container {
  padding: 24px;
  min-height: 100vh;
}

.map-box {
  width: 100%;
  height: 250px;
  border-radius: 4px;
}

.cover-uploader {
  :deep(.ant-upload) {
    width: 100%;
    height: 150px;
    border-radius: 2px;
    cursor: pointer;
    transition: border-color 0.3s;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;

    &:hover {
      border-color: #1890ff;
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mb-4 {
  margin-bottom: 16px;
}
.mb-2 {
  margin-bottom: 8px;
}
.mt-6 {
  margin-top: 24px;
}
</style>
