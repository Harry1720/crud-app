//Tự động đính kèm Authorization: Bearer <token> vào mọi request

import { HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { AuthService } from "../services/auth.service";

//HttpInterceptorFn: Để định nghĩa kiểu (Type). Trình biên dịch cần biết biến authInterceptor của bạn không phải là một hàm tính toán vớ vẩn, mà nó là một hàm đánh chặn theo chuẩn của Angular (HttpInterceptorFn). Việc khai báo type này giúp VS Code có thể gợi ý code cho bạn.

export const authInterceptor: HttpInterceptorFn = (req, next) => 
    /*
    req (Request): Đây là "gói hàng" (chứa URL, Body) mà bạn đang chuẩn bị gửi lên Server.
    
    next: Có nghĩa là "Cho qua". Nó là cái chốt gác. Bạn gọi next() tức là cho phép gói hàng tiếp tục bay đi đến trạm tiếp theo, hoặc bay thẳng lên Server.
    */    
{
    // Login là endpoint public. Không gửi access token cũ/hết hạn kèm request này,
    // vì Resource Server có thể từ chối token trước khi request tới controller.
    if (req.url.endsWith('/auth/login')) {
        return next(req);
    }

    const authService = inject(AuthService);
    const token = authService.getToken(); //lấy token từ AuthService nếu có

    if(token){
        const authReq = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
        /*
        Tại sao lại là req.clone()?: Trong Angular, đối tượng req là Immutable => không thể viết req.headers = ... được. Bạn bắt buộc phải nhân bản (clone) gói hàng ban đầu ra, tạo thành một gói hàng mới (authReq), và dán thêm cái tem Authorization vào gói hàng mới này.
    
        Bearer ${token}: Đây là chuẩn bảo mật. • Từ "Bearer" (người cầm/mang) báo hiệu cho server rằng: "Bất kỳ ai đang cầm token này thì có quyền truy cập, không cần kiểm tra thêm tên đăng nhập hay mật khẩu nữa". Trong 95% trường hợp thực tế, người ta dùng Bearer để gửi Access Token, nhưng về mặt kỹ thuật, bạn hoàn toàn có thể nhét các loại token khác vào sau chữ Bearer
        */
        
        return next(authReq); //Mở barie, cho phép cái gói hàng mới (đã dán token) bay lên Server.
    }
    return next(req);
    /*
    Dòng này chạy khi if (token) là false (nghĩa là user chưa đăng nhập, chưa có token). Lúc này, ta mở barie và cho phép gói hàng gốc (không dán token) bay lên Server.
    (Nếu API đó yêu cầu đăng nhập mà bạn gửi gói hàng không có token, Server sẽ từ chối và chửi lại bằng lỗi 401 Unauthorized. 
    Nhưng đó là việc của Server, trách nhiệm của Interceptor chỉ là: có vé thì gắn, không có vé thì thôi cứ cho qua).
    */
}
