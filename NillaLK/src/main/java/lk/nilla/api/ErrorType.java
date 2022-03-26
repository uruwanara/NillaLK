package lk.nilla.api;

public enum ErrorType {
	BadParameters,
	VerificationFailure,
	BadPayload,
	ExpiredToken,
	NotAuthenticated,
	ClaimError,
	Forbidden,
	CredentialError,
	UserDoesNotExist,
	UserTypeDoesNotExist,
	InternalError,
	WeakPassword,
	EmailNotVerified,
	EmailFormatError,
	EmailAlreadyExists,
	NoPasswordProvided,
	emptyResult
}
