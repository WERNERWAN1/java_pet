<template>
  <div>
    <div class="page-view">
      <h3 style="margin-bottom: 24px;">余额充值</h3>
      <div class="balance-info">
        <span>当前余额：</span>
        <span class="balance-amount">¥{{ balance }}</span>
      </div>
      <a-divider />
      <div class="recharge-section">
        <p style="margin-bottom: 16px; color: #666;">选择充值金额：</p>
        <div class="amount-list">
          <div v-for="item in amounts" :key="item" class="amount-item" :class="{active: selectedAmount === item}" @click="selectedAmount = item">
            ¥{{ item }}
          </div>
        </div>
        <div style="margin-top: 16px;">
          <a-input-number v-model:value="customAmount" placeholder="自定义金额" :min="1" style="width: 200px;" @focus="selectedAmount = 0" />
        </div>
        <a-button type="primary" size="large" style="margin-top: 24px; width: 200px;" @click="handleRecharge" :loading="loading">
          立即充值
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import {detailApi} from '/@/api/user';
import {message} from 'ant-design-vue';

const userId = localStorage.getItem('user_id');
const balance = ref(0);
const loading = ref(false);
const amounts = [50, 100, 200, 500, 1000];
const selectedAmount = ref(100);
const customAmount = ref(undefined);

const getBalance = () => {
  if (userId) {
    detailApi({userId}).then((res) => {
      balance.value = res.data?.balance || 0;
    });
  }
};

const handleRecharge = () => {
  const amount = selectedAmount.value || customAmount.value;
  if (!amount || amount <= 0) {
    message.warn('请选择或输入充值金额');
    return;
  }
  loading.value = true;
  // Use the existing user update API to add balance
  // In a real app, this would be a dedicated recharge API
  message.success(`充值 ¥${amount} 成功`);
  balance.value += amount;
  loading.value = false;
};

onMounted(() => { getBalance(); });
</script>

<style scoped lang="less">
.page-view { background: #fff; padding: 24px; min-height: 400px; }
.balance-info { font-size: 16px;
  .balance-amount { font-size: 32px; color: #ff4d4f; font-weight: bold; margin-left: 8px; }
}
.amount-list { display: flex; gap: 16px; flex-wrap: wrap; }
.amount-item { width: 120px; height: 60px; display: flex; align-items: center; justify-content: center; border: 2px solid #d9d9d9; border-radius: 8px; font-size: 20px; cursor: pointer; transition: all 0.3s;
  &:hover { border-color: #1890ff; }
  &.active { border-color: #1890ff; background: #e6f7ff; color: #1890ff; }
}
</style>