import { jsonRequest } from '@/services/api';

export const getUserInfo = async () => {

	const response = await jsonRequest(`/user`, 'GET');

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
};

export const updateUserInfo = async () => {

	const response = await jsonRequest(`/user`, 'PUT');

	if (!response || response.data === undefined) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return { success: true, message: "사용자 정보가 업데이트되었습니다." };
};

export const deleteUserInfo = async () => {

	const response = await jsonRequest(`/user`, 'DELETE');

	if (!response || response.data === undefined) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return { success: true, message: "사용자 계정이 삭제되었습니다." };
};