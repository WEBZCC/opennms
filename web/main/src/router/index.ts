import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import Nodes from '../views/Nodes.vue'
import NodeDetails from '../views/NodeDetails.vue'
import Inventory from '../views/Inventory.vue'

const routes: Array<RouteRecordRaw> = [
  {
    path: '',
    name: 'Nodes',
    component: Nodes
  },
  {
    path: '/node/:id',
    name: 'Node Details',
    component: NodeDetails
  },
  {
    path: '/inventory',
    name: 'Inventory',
    component: Inventory
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: "/"
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router
