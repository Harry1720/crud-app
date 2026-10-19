import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])) 
  ]
};

/*
 * providers (Các nhà cung cấp): Đây là một mảng danh sách. Bạn muốn ứng dụng của mình có khả năng gì (có Routing chuyển trang, có HTTP gọi API, có xử lý lỗi...), bạn phải "cung cấp" công cụ vào mảng này. 

 * provideBrowserGlobalErrorListeners(): Công cụ này giúp ứng dụng lắng nghe và bắt các lỗi toàn cục (global errors) xảy ra trên trình duyệt (ví dụ: lỗi crash giao diện, lỗi chưa được catch) để log ra màn hình console thay vì làm chết đứng cả trang web.
 
 * provideRouter(routes): Kích hoạt hệ thống Bộ định tuyến (Router). Nhờ có nó, ứng dụng của bạn mới biết rằng khi người dùng gõ link /login thì mở trang đăng nhập, gõ /users thì mở danh sách người dùng. Biến routes được import từ app.routes.ts chứa danh sách bản đồ các trang đó.
 
 *provideHttpClient(): Bật tính năng gọi API lên.  => Các file Service mới có thể dùng lệnh inject(HttpClient) để gọi API được. Nếu xóa dòng này, toàn bộ app sẽ báo lỗi "Không tìm thấy HttpClient".

  withInterceptors([...]): Đây là mệnh lệnh đi kèm. Nó nói với Angular rằng: "Ê, khi tạo ra đường cao tốc HTTP, nhớ mang các Trạm kiểm soát này ra đặt lên đường cho tôi".

  [authInterceptor]: Bạn truyền cái Interceptor bạn vừa viết vào đây. Lưu ý nó là một mảng [], nghĩa là bạn có thể tạo 10 cái interceptor khác nhau (ví dụ: 1 cái gắn token, 1 cái thêm loading xoay xoay, 1 cái bắt lỗi 500...) và xếp chúng thành một chuỗi trạm kiểm soát liên tiếp nhau.
 */