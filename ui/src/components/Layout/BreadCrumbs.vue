<template>
  <div class="breadcrumbs subtitle2">
    <template v-for="item of items">
      <div class="link">
        <router-link :to="item.to">{{ item.label }}</router-link>
        <FeatherIcon :icon="ChevronRight" :class="'link-icon' + ` ${item.position}`" />
      </div>
    </template>
  </div>
</template>
  
<script setup lang="ts">
import { PropType } from 'vue';
import { BreadCrumb } from '@/types';
import { FeatherIcon } from "@featherds/icon"
import ChevronRight from "@featherds/icon/navigation/ChevronRight"
defineProps({
  items: {
    required: true,
    type: Array as PropType<BreadCrumb[]>
  }
})
</script>
  
<style lang="scss" scoped>
@import "@featherds/styles/mixins/elevation";
.breadcrumbs {
  @include elevation(1);
  width: 100%;
  display: flex;
  height: 47px;
  margin-top: 15px;
  margin-bottom: 15px;
  .link {
    color: var(--feather-secondary-variant) !important;
    line-height: 3.5;
  }
  .link:first-child {
    margin-left: 15px;
  }
  .link-icon {
    margin: 0px 10px -3px 10px;
    font-size: 20px;
  }
  .last {
    display: none;
  }
}
</style>
