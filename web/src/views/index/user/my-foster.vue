<template>
  <div>
    <div class="page-view">
      <h3 style="margin-bottom: 16px;">我的寄养记录</h3>
      <a-table
          size="middle"
          rowKey="orderId"
          :loading="loading"
          :columns="columns"
          :data-source="list"
          :pagination="{ size: 'default', pageSize: 10, showTotal: (total) => `共${total}条数据` }"
      >
        <template #bodyCell="{ text, column }">
          <template v-if="column.key === 'progress'">
            <a-tag :color="Number(text) === 3 ? 'green' : Number(text) === 2 ? 'blue' : 'orange'">{{ progressText[Number(text)] || text }}</a-tag>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup>
import {listApi} from '/@/api/foster';

const loading = ref(false);
const list = ref([]);
const userId = localStorage.getItem('user_id');

// 寄养进度：1-待处理，2-寄养中，3-已完成，4-已领回，5-已预约
const progressText = {1: '待处理', 2: '寄养中', 3: '已完成', 4: '已领回', 5: '已预约'};

const columns = reactive([
  {title: '宠物名', dataIndex: 'pet_name', key: 'pet_name', align: 'center'},
  {title: '体重', dataIndex: 'pet_weight', key: 'pet_weight', align: 'center', customRender: ({text}) => text ? text + ' kg' : '-'},
  {title: '入住日期', dataIndex: 'start_date', key: 'start_date', align: 'center'},
  {title: '托管天数', dataIndex: 'duration', key: 'duration', align: 'center'},
  {title: '笼位编号', dataIndex: 'cage_no', key: 'cage_no', align: 'center'},
  {title: '状态', dataIndex: 'progress', key: 'progress', align: 'center'},
]);

const getDataList = () => {
  loading.value = true;
  listApi({}).then((res) => {
    loading.value = false;
    list.value = (res.data || []).filter(item => String(item.client_uid) === String(userId));
  }).catch(() => { loading.value = false; });
};

onMounted(() => { getDataList(); });
</script>

<style scoped lang="less">
.page-view { background: #fff; padding: 24px; min-height: 400px; }
</style>