import type { RouteRecordRaw } from 'vue-router'
import { AccessEnum } from '~@/utils/constant'
import { basicRouteMap } from './router-modules'

export default [
  {
    path: '/dashboard',
    redirect: '/dashboard/analysis',
    name: 'Dashboard',
    meta: {
      title: '仪表盘',
      icon: 'DashboardOutlined',
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/dashboard/analysis',
        name: 'DashboardAnalysis',
        component: () => import('~/pages/dashboard/analysis/index.vue'),
        meta: {
          title: '景点地图',
        },
      },
      {
        path: '/dashboard/monitor',
        name: 'DashboardMonitor',
        component: () => import('~/pages/dashboard/monitor/index.vue'),
        meta: {
          title: '监控页',
          access: [AccessEnum.ADMIN, AccessEnum.AUTH],
        },
      },
      {
        path: '/dashboard/workplace',
        name: 'DashboardWorkplace',
        component: () => import('~/pages/dashboard/workplace/index.vue'),
        meta: {
          title: '监控页',
          access: [AccessEnum.ADMIN],
        },
      },
    ],
  },
  {
    path: '/form',
    redirect: '/form/basic-form',
    name: 'Form',
    meta: {
      title: '景点发布',
      icon: 'FormOutlined',
      access: [AccessEnum.ADMIN, AccessEnum.AUTH],
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/form/basic-form/:id?',
        name: 'FormBasic',
        component: () => import('~/pages/form/basic-form/index.vue'),
        meta: {
          title: '景点发布',
          //locale: 'menu.form.basic-form',
        },
      },
    ],
  },
  {
    path: '/menu',
    redirect: '/menu/menu1',
    name: 'Menu',
    meta: {
      title: '功能',
      icon: 'BarsOutlined',
      access: [AccessEnum.ADMIN],
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/menu/menu1',
        name: 'MenuMenu11',
        component: () => import('~/pages/menu/menu1.vue'),
        meta: {
          title: '用户管理',

        },
      },
    ],
  },

  {
    path: '/list',
    redirect: '/list/card-list',
    name: 'List',
    meta: {
      title: '景点列表',
      icon: 'TableOutlined',
      //locale: 'menu.list',
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/list/card-list',
        name: 'CardList',
        component: () => import('~/pages/list/card-list.vue'),
        meta: {
          title: '景点列表',
          //locale: 'menu.list.card-list',
        },
      },
      {
        path: '/list/crud-table',
        name: 'CrudTable',
        component: () => import('~/pages/list/crud-table.vue'),
        meta: {
          title: '景点管理',
          //locale: 'menu.list.crud-table',
          access: [AccessEnum.ADMIN, AccessEnum.AUTH],
        },
      },
      {
        path: '/list/search-list',
        name: 'SearchList',
        component: () => import('~/pages/list/search-list/index.vue'),
        meta: {
          title: '搜索',
          //locale: 'menu.list.search-list',
        },
        redirect: '/list/search-list/articles',
        children: [
          {
            path: '/list/search-list/articles',
            name: 'SearchListArticles',
            component: () => import('~/pages/list/search-list/articles.vue'),
            meta: {
              title: '搜索景点',
              //locale: 'menu.list.search-list.articles',
            },
          },
        ],
      },
    ],
  },
  {
    path: '/articledetail/:id',
    name: 'ArticleDetail',
    component: () => import('~/pages/Article/ArticleDetail.vue'),
    meta: {
      title: '文章详情',
      hideInMenu: true,
    }

  },
  {
    path: '/userdetail/:id',
    name: 'UserDetail',
    component: () => import('~/pages/User/userDetail.vue'),
    meta: {
      title: '用户信息',
      hideInMenu: true,
    }
  },
  {
    path: '/account',
    redirect: '/account/center',
    name: 'Account',
    meta: {
      title: '个人页',
      icon: 'UserOutlined',
      locale: 'menu.account',
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/account/settings',
        name: 'AccountSettings',
        component: () => import('~/pages/account/settings.vue'),
        meta: {
          title: '个人设置',
          locale: 'menu.account.settings',
        },
      },
      {
        path: '/account/settings/:id',
        name: 'AccountSettings1',
        component: () => import('~/pages/account/settings.vue'),
        meta: {
          title: '个人设置1',
          locale: 'menu.account.settings',
          hideInMenu: true,
          parentKeys: ['/account/settings'],
        },
      },
    ],
  },
] as RouteRecordRaw[]
