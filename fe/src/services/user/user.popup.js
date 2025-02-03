import { jsonRequest, multipartRequest } from '@/services/api';

export const popupList = async (param) => {

	const query = param
        ? '?' + new URLSearchParams(param).toString()
        : '';
	
	const response = await jsonRequest(`/popup/user${query}`, 'GET');

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
};

export const popupView = async (id) => {
	const response = await jsonRequest(`/popup/${id}`, 'GET');

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
}

export const popupDelete = async (id) => {
	await jsonRequest(`/popup/${id}`, 'DELETE')

	return { success: true, message: "팝업 스토어가 삭제되었습니다." };
}

export const popupInsert = async (data) => {
	await multipartRequest(`/popup`, 'POST', data)

	return { success: true, message: "팝업 스토어가 추가되었습니다." };
}

export const popupUpdate = async (id, data) => {
	await multipartRequest(`/popup/${id}`, 'PUT', data)

	return { success: true, message: "팝업 스토어가 수정되었습니다." };
}

export const statusChangePopup = async (id, data) => {
	await jsonRequest(`/popup/${id}`, 'PATCH', data)

	return { success: true, message: "팝업 스토어의 상태가 변경되었습니다." };
}