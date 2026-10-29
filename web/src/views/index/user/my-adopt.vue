<template>
  <div>
    <div class="page-view">
      <h3 style="margin-bottom: 16px;">我的领养记录</h3>
      <a-table
          size="middle"
          rowKey="recordId"
          :loading="loading"
          :columns="columns"
          :data-source="list"
          :pagination="{ size: 'default', pageSize: 10, showTotal: (total) => `共${total}条数据` }"
      >
        <template #bodyCell="{ text, record, column }">
          <template v-if="column.key === 'state'">
            <a-tag :color="Number(text) === 2 ? 'green' : Number(text) === 3 ? 'red' : 'blue'">{{ stateText[Number(text)] || text }}</a-tag>
          </template>
          <template v-if="column.key === 'operation'">
            <a-popconfirm v-if="Number(record.state) === 2" title="确定退养?" ok-text="是" cancel-text="否" @confirm="handleReturn(record)">
              <a style="color: #ff4d4f;">退养</a>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup>
import {listApi, updateApi} from '/@/api/adoptionRecord';
import {message} from 'ant-design-vue';

const loading = ref(false);
const list = ref([]);
const userId = localStorage.getItem('user_id');

// 领养状态：1-待审核，2-已领养，3-已退养，4-已归还
const stateText = {1: '待审核', 2: '已领养', 3: '已退养', 4: '已归还'};

const columns = reactive([
  {title: '宠物名', dataIndex: 'pet_name', key: 'pet_name', align: 'center'},
  {title: '领养日期', dataIndex: 'adopt_date', key: 'adopt_date', align: 'center'},
  {title: '状态', dataIndex: 'state', key: 'state', align: 'center'},
  {title: '操作', dataIndex: 'action', key: 'operation', align: 'center', width: 100},
]);

const getDataList = () => {
  loading.value = true;
  listApi({}).then((res) => {
    loading.value = false;
    list.value = (res.data || []).filter(item => String(item.owner_uid) === String(userId));
  }).catch(() => { loading.value = false; });
};

const handleReturn = (record) => {
  updateApi({recordId: record.recordId, state: 3}).then(() => { // 3-已退养
    message.success('退养成功');
    getDataList();
  }).catch(() => { message.error('操作失败'); });
};

onMounted(() => { getDataList(); });
</script>

<style scoped lang="less">
.page-view { background: #fff; padding: 24px; min-height: 400px; }
</style>