import { jsonRequest } from '@/services/api';

export const landView = async (landId) => {

	const response = await jsonRequest(`/land/view/${landId}`, 'GET');

	// 응답 데이터가 유효한지 확인
	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
};