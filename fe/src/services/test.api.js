import { jsonRequest } from '@/services/api';

export const testApi = async () => {
	const response = await jsonRequest('/test/data', 'GET');

	// 응답 데이터가 유효한지 확인
	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
};