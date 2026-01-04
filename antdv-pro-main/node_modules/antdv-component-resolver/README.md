# antdv-component-resolver
this is antdv component resolver


## Install

```bash

npm install antdv-component-resolver -D

```

## Useage

vite.config.ts

```ts
import { defineConfig } from 'vite'
import Components from 'unplugin-vue-components/vite'
import AntdvResolver from 'antdv-component-resolver'
export default defineConfig({
  plugins:[
    Components({
        resolvers: [AntdvResolver()],
      }),
  ]
})

```
