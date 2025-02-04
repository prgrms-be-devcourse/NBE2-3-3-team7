import { jsonRequest } from '@/services/api';

export const homeItem = async () => {

	const response = await jsonRequest(`/home`, 'GET');

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
};