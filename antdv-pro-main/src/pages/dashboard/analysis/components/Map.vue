<template>
  <div class="map-wrapper">
    <div id="map" class="map-container"></div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, watch, onBeforeUnmount } from 'vue';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { useRouter } from 'vue-router';

// 假设你的 ArticleVO 定义在一个单独的文件里，如果没有请保留你原来的 interface 定义
// import { ArticleVO } from '@/types/Entity'; 
export interface ArticleVO {
    id: string;
    title: string;
    thumbnail: string;
    latitude: number;
    longitude: number;
    name: string;
    // ... 其他字段
}

const props = withDefaults(defineProps<{
  spotList: any[]
}>(), {
  spotList: () => []
});

const router = useRouter();
let map: L.Map | null = null;
let markersLayer: L.LayerGroup | null = null; // 【1】新增：用于管理标记的图层组

// 【2】解决弹窗点击跳转无效的问题
// Leaflet 的 Popup 是纯 HTML 字符串，onclick 只能调用 window 全局对象下的方法
// 所以我们将跳转逻辑挂载到 window 上
onMounted(() => {
  (window as any).gotoArticleDetail = (id: any) => {
    router.push({ name: 'ArticleDetail', params: { id: id } });
  };
  initMap();
});

onBeforeUnmount(() => {
  // 组件销毁时清理全局变量
  delete (window as any).gotoArticleDetail;
});

// 【3】核心修复：监听数据变化
watch(() => props.spotList, (newVal) => {
  // 只有当地图初始化完成，且有新数据时才渲染
  if (map && newVal && newVal.length > 0) {
    renderMarkers(newVal);
  }
});

const initMap = () => {
  // 1. 初始化地图容器
  map = L.map('map').setView([39.90923, 116.397428], 13);

  L.tileLayer('http://webrd01.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}', {
    maxZoom: 18,
    attribution: '© Gaode Map'
  }).addTo(map);

  // 2. 初始化标记图层组
  markersLayer = L.layerGroup().addTo(map);

  // 3. 如果初始化时数据已经有了（极少情况，但为了保险），也渲染一次
  if (props.spotList.length > 0) {
    renderMarkers(props.spotList);
  }
};

// 【4】抽离渲染逻辑，支持反复调用
const renderMarkers = (list: any[]) => {
  if (!map || !markersLayer) return;

  // 先清除旧的标记，防止重复添加
  markersLayer.clearLayers();

  list.forEach(spot => {
    // 安全校验：防止经纬度缺失导致报错
    if (!spot.latitude || !spot.longitude) return;

    const customIcon = L.icon({
      iconUrl: spot.thumbnail || 'https://cdn-icons-png.flaticon.com/512/252/252025.png', // 增加默认图防止报错
      iconSize: [40, 40],
      iconAnchor: [20, 40],
      popupAnchor: [0, -40]
    });

    const marker = L.marker([spot.latitude, spot.longitude], { icon: customIcon });

    // 修改 onclick 调用方式，调用我们在 onMounted 挂载的全局函数
    marker.bindPopup(`
      <div style="text-align: center">
        <h4 style="margin: 5px 0;">${spot.name}</h4>
        <img src="${spot.thumbnail}" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;">
        <br>
        <button onclick="window.gotoArticleDetail('${spot.id}')" 
                style="margin-top:5px; cursor:pointer; padding: 4px 8px; border:1px solid #ccc; background:#fff; border-radius:4px;">
          查看详情
        </button>
      </div>
    `);

    // 将标记添加到图层组，而不是直接添加到 map
    markersLayer!.addLayer(marker);
  });
  
  // 可选：自动调整地图视野以包含所有标记
  if (list.length > 0) {
     const group = L.featureGroup(markersLayer.getLayers() as any);
     if(group.getBounds().isValid()){
         map.fitBounds(group.getBounds());
     }
  }
};
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 500px;
  border-radius: 8px;
  z-index: 1;
}
</style>
