# NUMO

## Error handling on client side

When some error happens server responds with StatusRuntimeException object.
StatusRuntimeException consists of Status object `status` and Metadata object `trailers`.

Status object contains `code` and `description`.
Code is one of 16 predefined GRPC general error codes and description is error description in English.

Metadata is basically a key/value data structure.
The Metadata `trailers` always contains `error_code` key.
The `error_code` is one of enum values ErrorCode defined in `Error.proto`.

The `error_code` value should be used as key to find your customized/internalized error messages for user.
If you haven't defined error message for the `error_code` then `description` from Status object can be used.
