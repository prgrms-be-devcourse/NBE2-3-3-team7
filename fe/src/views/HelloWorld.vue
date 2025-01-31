<script setup>
import { ref, onMounted } from 'vue'
import { countStore } from '@/store/count';
import { testApi } from '@/services/test.api'
import { popupList } from '@/services/popup/popup.list';

const store = countStore();

// 상태 관리
const count = ref(store.count) // store의 count 상태를 가져옴

const apiData = ref(null); // API에서 가져온 데이터를 저장할 변수
const loading = ref(false); // 데이터 로딩 상태
const error = ref(null); // 에러 상태

onMounted(() => {
	fetchApiData(); // API 데이터 호출
	fetchPopupList();
});

const increment = () => {
	store.increment(); // store의 increment 함수 호출
	count.value = store.count; // count 상태 업데이트
};

const fetchApiData = async () => {
	loading.value = true;
	error.value = null;

	try {
		const result = await testApi(); // 에러 발생 시 catch로 이동
		apiData.value = result;
	} catch (err) {
		error.value = err instanceof Error ? err.message : '알 수 없는 오류 발생';
		console.error('API 요청 오류:', err);
	} finally {
		loading.value = false;
	}
};

const fetchPopupList = async () => {

	try {
		const result = await popupList(); // 에러 발생 시 catch로 이동
		apiData.value = result;
	} catch (err) {
		error.value = err instanceof Error ? err.message : '알 수 없는 오류 발생';
		console.error('API 요청 오류:', err);
	} finally {
		loading.value = false;
	}
};

</script>

<template>
	<div class="card">
		<button type="button" @click="increment">count is {{ count }}</button>
	</div>
	<!-- API 호출 결과 출력 -->
	<div v-if="loading">Loading...</div>
	<div v-if="error" class="error">{{ error }}</div>
	<div v-if="apiData">
		<span>API Data : </span>
		<span>{{ apiData }}</span>
	</div>
	<div>
		<!-- Test Data -->
		<span>Popup Data : </span>
		<span>{{ popupData }}</span>
	</div>
</template>

<style scoped>
</style>
