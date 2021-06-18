<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="GG2">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n4">
            <attr name="layout">
                <string>309 172 14 18</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>342 282 27 36</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>344 212 27 36</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>219 246 39 36</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>228 153 27 36</string>
            </attr>
        </node>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>int:80</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>type:Phil</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>flag:hungry</string>
            </attr>
        </edge>
        <edge from="n1" to="n3">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge from="n1" to="n4">
            <attr name="label">
                <string>forks</string>
            </attr>
        </edge>
        <edge from="n1" to="n0">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n1" to="n2">
            <attr name="label">
                <string>right</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>type:Fork</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>flag:free</string>
            </attr>
        </edge>
    </graph>
</gxl>
