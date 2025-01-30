import { jsonRequest } from '@/services/api';

export const popupList = async (param) => {
	const response = await jsonRequest(`/popup/list?${param}`, 'GET');

	// 응답 데이터가 유효한지 확인
	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
};


/** 
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
*/