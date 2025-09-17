import base64
import subprocess

with open("file.jpg", "rb") as image_file:
    encoded = base64.b64encode(image_file.read()).decode("utf-8")
    subprocess.run("clip", text=True, input=encoded)
    print("image encoded copied to clipboard")