import { jsonRequest, multipartRequest } from '@/services/api';

export const landList = async (param) => {

	const query = param
        ? '?' + new URLSearchParams(param).toString()
        : '';
	
	const response = await jsonRequest(`/land/user${query}`, 'GET');

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
};

export const landView = async (id) => {
	const response = await jsonRequest(`/land/${id}`, 'GET');

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
}

export const landDelete = async (id) => {
	await jsonRequest(`/land/${id}`, 'DELETE')

	return { success: true, message: "임대지가 삭제되었습니다." };
}

export const landInsert = async (data) => {
	await multipartRequest(`/land`, 'POST', data)

	return { success: true, message: "임대지가 추가되었습니다." };
}

export const landUpdate = async (id, data) => {
	await multipartRequest(`/land/${id}`, 'PUT', data)

	return { success: true, message: "임대지가 수정되었습니다." };
}

export const statusChangeLand = async (id, data) => {
	await jsonRequest(`/land/${id}`, 'PATCH', data)

	return { success: true, message: "임대지의 상태가 변경되었습니다." };
}